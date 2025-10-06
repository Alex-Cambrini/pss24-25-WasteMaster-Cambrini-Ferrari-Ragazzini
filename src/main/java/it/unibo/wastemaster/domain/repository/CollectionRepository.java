package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Collection entities.
 * Provides CRUD operations and various retrieval methods based on schedule, status,
 * date, postal code, and customer.
 */
public interface CollectionRepository {

    /**
     * Retrieves all collections associated with the given schedule.
     *
     * @param schedule the Schedule entity to filter collections
     * @return a list of Collection entities matching the schedule
     */
    List<Collection> findAllBySchedule(Schedule schedule);

    /**
     * Retrieves all collections with the specified status.
     *
     * @param status the CollectionStatus to filter collections
     * @return a list of Collection entities with the given status
     */
    List<Collection> findByStatus(CollectionStatus status);

    /**
     * Retrieves the currently active collection for the given recurring schedule, if any.
     *
     * @param schedule the RecurringSchedule to find the active collection for
     * @return an Optional containing the active Collection if present, empty otherwise
     */
    Optional<Collection> findActiveByRecurringSchedule(RecurringSchedule schedule);

    /**
     * Retrieves all collections for a specific postal code on a given date.
     *
     * @param postalCode the postal code to filter collections
     * @param date the date to filter collections
     * @return a list of Collection entities matching the postal code and date
     */
    List<Collection> findCollectionsByPostalCodeAndDate(String postalCode,
                                                        LocalDate date);

    /**
     * Retrieves all completed collections for a customer that have not yet been billed.
     *
     * @param customer the Customer entity to filter collections
     * @return a list of completed and not billed Collection entities for the customer
     */
    List<Collection> findCompletedNotBilledByCustomer(Customer customer);

    /**
     * Updates an existing collection.
     *
     * @param collection the Collection entity to update
     */
    void update(Collection collection);

    /**
     * Persists a new collection.
     *
     * @param collection the Collection entity to save
     */
    void save(Collection collection);

    /**
     * Deletes a collection.
     *
     * @param collection the Collection entity to delete
     */
    void delete(Collection collection);

    /**
     * Retrieves all collections in the system.
     *
     * @return a list of all Collection entities
     */
    List<Collection> findAll();
}
