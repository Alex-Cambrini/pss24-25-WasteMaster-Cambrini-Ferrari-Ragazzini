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

public class CollectionRepositoryImpl implements CollectionRepository {

    private final CollectionDAO collectionDAO;

    public CollectionRepositoryImpl(CollectionDAO collectionDAO) {
        this.collectionDAO = collectionDAO;
    }

    @Override
    public List<Collection> findAllBySchedule(Schedule schedule) {
        return collectionDAO.findAllCollectionsBySchedule(schedule);
    }

    @Override
    public List<Collection> findByStatus(CollectionStatus status) {
        return collectionDAO.findCollectionByStatus(status);
    }

    @Override
    public Optional<Collection> findActiveByRecurringSchedule(
            RecurringSchedule schedule) {
        return Optional.ofNullable(
                collectionDAO.findActiveCollectionByRecurringSchedule(schedule));
    }

    @Override
    public List<Collection> findByDateRange(LocalDate start, LocalDate end) {
        return collectionDAO.findByDateRange(start, end);
    }

    @Override
    public List<Collection> findCollectionsByPostalCodeAndDate(String postalCode,  LocalDate date) {
        return collectionDAO.findCollectionsByPostalCodeAndDate(postalCode, date);
    }

    @Override
    public List<Collection> findCompletedNotBilledByCustomer(final Customer customer) {
        return collectionDAO.findCompletedNotBilledByCustomer(customer);
    }

    @Override
    public void update(Collection collection) {
        collectionDAO.update(collection);
    }

    @Override
    public void save(Collection collection) {
        collectionDAO.insert(collection);
    }

    @Override
    public void delete(Collection collection) {
        collectionDAO.delete(collection);
    }
    
    @Override
    public List<Collection> findAll() {
        return collectionDAO.findAll();
    }
}
