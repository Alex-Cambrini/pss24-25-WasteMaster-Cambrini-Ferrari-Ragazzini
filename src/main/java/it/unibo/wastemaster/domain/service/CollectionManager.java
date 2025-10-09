package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.factory.CollectionFactory;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Manages the creation, retrieval, update, and cancellation of waste collections,
 * including operations for one-time and recurring schedules.
 */
public class CollectionManager {

    private final CollectionRepository collectionRepository;
    private final RecurringScheduleManager recurringScheduleManager;
    private final CollectionFactory collectionFactory;  // aggiungi questo attributo

    /**
     * Constructs a CollectionManager with the necessary dependencies.
     *
     * @param collectionRepository DAO used for Collection persistence, must not be null
     * @param recurringScheduleManager Manager for recurring schedule logic, must not
     * be null
     * @param collectionFactory Factory responsible for creating Collection instances,
     * must not be null
     */
    public CollectionManager(final CollectionRepository collectionRepository,
                             final RecurringScheduleManager recurringScheduleManager,
                             final CollectionFactory collectionFactory) {
        this.collectionRepository = collectionRepository;
        this.recurringScheduleManager = recurringScheduleManager;
        this.collectionFactory = collectionFactory;
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
     * Retrieves the currently active collection associated with the given recurring
     * schedule, if present.
     *
     * @param schedule the recurring schedule
     * @return an Optional containing the active collection if found, or an empty
     * Optional otherwise
     */
    public Optional<Collection> getActiveCollectionByRecurringSchedule(
            final RecurringSchedule schedule) {
        return collectionRepository.findActiveByRecurringSchedule(schedule);
    }

    /**
     * Checks if the collection date of the given schedule is in the future.
     *
     * @param schedule the schedule to validate
     * @return true if the collection date is after today, false otherwise
     */
    private boolean isCollectionDateValid(final Schedule schedule) {
        return schedule.getCollectionDate().isAfter(LocalDate.now());
    }

    /**
     * Generates a collection for a one-time schedule if its collection date is in the
     * future.
     * Uses the CollectionFactory to create the collection and saves it in the repository.
     *
     * @param schedule the one-time schedule for which to generate a collection
     */
    public void generateOneTimeCollection(final OneTimeSchedule schedule) {
        if (isCollectionDateValid(schedule)) {
            Collection collection = collectionFactory.createOneTimeCollection(schedule);
            collectionRepository.save(collection);
        }
    }

    /**
     * Generates and persists a collection for a given recurring schedule.
     * The method first checks if the schedule's next collection date is in the future.
     * If valid, it uses the injected {@link CollectionFactory} to create a new
     * {@link Collection} instance and saves it via the repository.
     *
     * @param schedule the recurring schedule for which to generate a collection; must
     * not be null
     * @throws IllegalArgumentException if the schedule is null
     */
    public void generateRecurringCollection(final RecurringSchedule schedule) {
        if (isCollectionDateValid(schedule)) {
            Collection collection = collectionFactory.createRecurringCollection(schedule);
            collectionRepository.save(collection);
        }
    }

    /**
     * Generates collections for all upcoming recurring schedules that do not yet have
     * a collection,
     * only if their collection dates are in the future. Uses the CollectionFactory to
     * create each
     * collection and saves it in the repository.
     */
    public void generateRecurringCollections() {
        final List<RecurringSchedule> upcomingSchedules = recurringScheduleManager
                .getRecurringSchedulesWithoutCollections();
        for (final RecurringSchedule schedule : upcomingSchedules) {
            if (isCollectionDateValid(schedule)) {
                Collection collection =
                        collectionFactory.createRecurringCollection(schedule);
                collectionRepository.save(collection);
            }
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
     * Retrieves all completed collections for a given customer that have not yet
     * been billed.
     *
     * @param customer the customer whose collections to retrieve
     * @return list of completed but not billed collections for the customer
     * @throws IllegalArgumentException if the customer is null
     */
    public List<Collection> getCompletedNotBilledCollections(final Customer customer) {
        ValidateUtils.requireArgNotNull(customer, "Customer cannot be null");
        return collectionRepository.findCompletedNotBilledByCustomer(customer);
    }

    /**
     * Retrieves all collections for a specific postal code on a given date.
     *
     * @param postalCode the postal code to filter collections by
     * @param date the date for which to retrieve collections
     * @return a list of collections matching the postal code and date
     * @throws IllegalArgumentException if the postal code is null
     */
    public List<Collection> getCollectionsByPostalCode(final String postalCode,
                                                       final LocalDate date) {
        ValidateUtils.requireArgNotNull(postalCode, "Postal code cannot be null");
        return collectionRepository.findCollectionsByPostalCodeAndDate(postalCode, date);
    }

    /**
     * Retrieves all collections present in the system.
     *
     * @return a list of all collections
     */
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }
}
