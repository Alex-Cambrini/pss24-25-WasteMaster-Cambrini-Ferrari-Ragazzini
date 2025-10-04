package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import it.unibo.wastemaster.infrastructure.dao.CollectionDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CollectionRepository} that uses {@link CollectionDAO}
 * to perform CRUD operations on Collection entities.
 */
public class CollectionRepositoryImpl implements CollectionRepository {

    private final CollectionDAO collectionDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param collectionDAO the DAO used to access collection data
     */
    public CollectionRepositoryImpl(final CollectionDAO collectionDAO) {
        this.collectionDAO = collectionDAO;
    }

    /**
     * Retrieves all collections linked to a specific schedule.
     *
     * @param schedule the schedule to filter collections by
     * @return a list of collections associated with the schedule
     */
    @Override
    public List<Collection> findAllBySchedule(final Schedule schedule) {
        return collectionDAO.findAllCollectionsBySchedule(schedule);
    }

    /**
     * Retrieves collections by their status.
     *
     * @param status the status of collections to retrieve
     * @return a list of collections with the specified status
     */
    @Override
    public List<Collection> findByStatus(final CollectionStatus status) {
        return collectionDAO.findCollectionByStatus(status);
    }

    /**
     * Retrieves the active collection associated with a recurring schedule.
     *
     * @param schedule the recurring schedule to search by
     * @return an Optional containing the active collection if found, or empty
     */
    @Override
    public Optional<Collection> findActiveByRecurringSchedule(
            final RecurringSchedule schedule) {
        return Optional.ofNullable(
                collectionDAO.findActiveCollectionByRecurringSchedule(schedule));
    }

    /**
     * Retrieves collections for a specific postal code and date.
     *
     * @param postalCode the postal code to filter collections
     * @param date the date to filter collections
     * @return a list of collections matching the postal code and date
     */
    @Override
    public List<Collection> findCollectionsByPostalCodeAndDate(final String postalCode,
                                                               final LocalDate date) {
        return collectionDAO.findCollectionsByPostalCodeAndDate(postalCode, date);
    }

    /**
     * Retrieves completed but not billed collections for a specific customer.
     *
     * @param customer the customer to filter collections by
     * @return a list of completed, unbilled collections for the customer
     */
    @Override
    public List<Collection> findCompletedNotBilledByCustomer(final Customer customer) {
        return collectionDAO.findCompletedNotBilledByCustomer(customer);
    }

    /**
     * Updates an existing collection.
     *
     * @param collection the collection to update
     */
    @Override
    public void update(final Collection collection) {
        collectionDAO.update(collection);
    }

    /**
     * Persists a new collection.
     *
     * @param collection the collection to save
     */
    @Override
    public void save(final Collection collection) {
        collectionDAO.insert(collection);
    }

    /**
     * Deletes an existing collection.
     *
     * @param collection the collection to delete
     */
    @Override
    public void delete(final Collection collection) {
        collectionDAO.delete(collection);
    }

    /**
     * Retrieves all collections.
     *
     * @return a list of all collections
     */
    @Override
    public List<Collection> findAll() {
        return collectionDAO.findAll();
    }
}
