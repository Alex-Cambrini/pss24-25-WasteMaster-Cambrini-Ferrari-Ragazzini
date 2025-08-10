package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Manages the creation, retrieval, and cancellation of waste collections.
 */
public class CollectionManager {

    private static final int FIRST_HALF_START_MONTH = 1;
    private static final int FIRST_HALF_END_MONTH = 6;
    private static final int SECOND_HALF_START_MONTH = 7;
    private static final int SECOND_HALF_END_MONTH = 12;
    private static final int FIRST_DAY = 1;
    private static final int LAST_DAY_FIRST_HALF = 30;
    private static final int LAST_DAY_SECOND_HALF = 31;
    private final CollectionRepository collectionRepository;
    private final RecurringScheduleManager recurringScheduleManager;

    /**
     * Constructs a CollectionManager with the necessary dependencies.
     *
     * @param collectionRepository DAO used for Collection persistence
     * @param recurringScheduleManager Manager for recurring schedule logic
     */
    public CollectionManager(final CollectionRepository collectionRepository,
            final RecurringScheduleManager recurringScheduleManager) {
        this.collectionRepository = collectionRepository;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    /**
     * Retrieves all collections with a specific status.
     *
     * @param status the status to filter by
     * @return a list of collections with the given status
     */
    public List<Collection> getCollectionsByStatus(final CollectionStatus status) {
        return collectionRepository.findByStatus(status);
    }

    /**
     * Retrieves all collections associated with a given schedule.
     *
     * @param schedule the schedule to look up
     * @return list of associated collections
     */
    public List<Collection> getAllCollectionBySchedule(final Schedule schedule) {
        return collectionRepository.findAllBySchedule(schedule);
    }

    /**
     * Generates a collection for a schedule if the collection date is in the future.
     *
     * @param schedule the schedule to generate a collection for
     */
    public void generateCollection(final Schedule schedule) {
        if (schedule.getCollectionDate().isAfter(LocalDate.now())) {
            final Collection collection = new Collection(schedule);
            collectionRepository.save(collection);
        }
    }

    /**
     * Retrieves the currently active collection associated with a given recurring
     * schedule.
     *
     * @param schedule the recurring schedule
     * @return the active collection, or null if none exists
     */
    public Optional<Collection> getActiveCollectionByRecurringSchedule(
            final RecurringSchedule schedule) {
        return collectionRepository.findActiveByRecurringSchedule(schedule);
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
        collectionRepository.update(collection);
    }

    /**
     * Retrieves the collections for the first half of the specified year.
     *
     * @param year the year to filter collections by
     * @return a list of collections occurring between January 1 and June 30 of the given
     *         year
     */
    public List<Collection> getFirstHalfCollections(final int year) {
        LocalDate start = LocalDate.of(year, FIRST_HALF_START_MONTH, FIRST_DAY);
        LocalDate end = LocalDate.of(year, FIRST_HALF_END_MONTH, LAST_DAY_FIRST_HALF);
        return collectionRepository.findByDateRange(start, end);
    }

    /**
     * Retrieves the collections for the second half of the specified year.
     *
     * @param year the year to filter collections by
     * @return a list of collections occurring between July 1 and December 31 of the given
     *         year
     */
    public List<Collection> getSecondHalfCollections(final int year) {
        LocalDate start = LocalDate.of(year, SECOND_HALF_START_MONTH, FIRST_DAY);
        LocalDate end = LocalDate.of(year, SECOND_HALF_END_MONTH, LAST_DAY_SECOND_HALF);
        return collectionRepository.findByDateRange(start, end);
    }
}
