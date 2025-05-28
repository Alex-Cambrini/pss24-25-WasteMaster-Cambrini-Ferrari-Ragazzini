package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import java.time.LocalDate;
import java.util.List;

/**
 * Manages the creation, retrieval, and cancellation of waste collections.
 */
public class CollectionManager {

    private final CollectionDAO collectionDAO;
    private final RecurringScheduleManager recurringScheduleManager;

    /**
     * Constructs a CollectionManager with the necessary dependencies.
     *
     * @param collectionDAO DAO used for Collection persistence
     * @param recurringScheduleManager Manager for recurring schedule logic
     */
    public CollectionManager(final CollectionDAO collectionDAO,
            final RecurringScheduleManager recurringScheduleManager) {
        this.collectionDAO = collectionDAO;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    /**
     * Retrieves all collections with a specific status.
     *
     * @param status the status to filter by
     * @return a list of collections with the given status
     */
    public List<Collection> getCollectionsByStatus(final CollectionStatus status) {
        return collectionDAO.findCollectionByStatus(status);
    }

    /**
     * Retrieves all collections associated with a given schedule.
     *
     * @param schedule the schedule to look up
     * @return list of associated collections
     */
    public List<Collection> getAllCollectionBySchedule(final Schedule schedule) {
        return collectionDAO.findAllCollectionsBySchedule(schedule);
    }

    /**
     * Generates a collection for a schedule if the collection date is in the future.
     *
     * @param schedule the schedule to generate a collection for
     */
    public void generateCollection(final Schedule schedule) {
        if (schedule.getCollectionDate().isAfter(LocalDate.now())) {
            final Collection collection = new Collection(schedule);
            collectionDAO.insert(collection);
        }
    }

    /**
     * Generates a collection specifically for a one-time schedule.
     *
     * @param schedule the one-time schedule
     */
    public void generateOneTimeCollection(final OneTimeSchedule schedule) {
        generateCollection(schedule);
    }

    /**
     * Generates upcoming collections for all recurring schedules without an assigned
     * collection.
     */
    public void generateRecurringCollections() {
        final List<RecurringSchedule> upcomingSchedules =
                recurringScheduleManager.getRecurringSchedulesWithoutCollections();
        for (final RecurringSchedule schedule : upcomingSchedules) {
            generateCollection(schedule);
        }
    }

    /**
     * Attempts to cancel a collection, if not already cancelled.
     *
     * @param collection the collection to cancel
     * @return true if cancellation was successful, false otherwise
     * @throws IllegalArgumentException if the collection or its ID is null
     */
    public boolean softDeleteCollection(final Collection collection) {
        ValidateUtils.requireArgNotNull(collection, "Collection cannot be null");
        ValidateUtils.requireArgNotNull(collection.getCollectionId(),
                "Collection ID cannot be null");

        if (collection.getCollectionStatus() == CollectionStatus.CANCELLED) {
            return false;
        }

        collection.setCollectionStatus(CollectionStatus.CANCELLED);
        updateCollection(collection);
        return true;
    }

    /**
     * Updates the collection in the database.
     *
     * @param collection the collection to update
     */
    public void updateCollection(final Collection collection) {
        collectionDAO.update(collection);
    }

    /**
     * Retrieves the currently active collection associated with a given recurring
     * schedule.
     *
     * @param schedule the recurring schedule
     * @return the active collection, or null if none exists
     */
    public Collection getActiveCollectionByRecurringSchedule(
            final RecurringSchedule schedule) {
        return collectionDAO.findActiveCollectionByRecurringSchedule(schedule);
    }
}
