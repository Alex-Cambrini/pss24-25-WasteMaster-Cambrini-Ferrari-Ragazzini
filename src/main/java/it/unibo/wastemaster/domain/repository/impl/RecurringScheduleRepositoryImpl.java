package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.repository.RecurringScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.RecurringScheduleDAO;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link RecurringScheduleRepository} that uses
 * {@link RecurringScheduleDAO}
 * to perform CRUD operations on RecurringSchedule entities.
 */
public class RecurringScheduleRepositoryImpl implements RecurringScheduleRepository {

    private final RecurringScheduleDAO recurringScheduleDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param recurringScheduleDAO the DAO used to access recurring schedule data
     */
    public RecurringScheduleRepositoryImpl(
            final RecurringScheduleDAO recurringScheduleDAO) {
        this.recurringScheduleDAO = recurringScheduleDAO;
    }

    /**
     * Persists a new recurring schedule.
     *
     * @param schedule the schedule to save
     */
    @Override
    public void save(final RecurringSchedule schedule) {
        recurringScheduleDAO.insert(schedule);
    }

    /**
     * Updates an existing recurring schedule.
     *
     * @param schedule the schedule to update
     */
    @Override
    public void update(final RecurringSchedule schedule) {
        recurringScheduleDAO.update(schedule);
    }

    /**
     * Retrieves a recurring schedule by its ID.
     *
     * @param id the schedule ID
     * @return an Optional containing the schedule if found, or empty
     */
    @Override
    public Optional<RecurringSchedule> findById(final Integer id) {
        return recurringScheduleDAO.findById(id);
    }

    /**
     * Retrieves active schedules without future collections.
     *
     * @return a list of active schedules without future collections
     */
    @Override
    public List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections() {
        return recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();
    }

    /**
     * Retrieves active schedules whose next collection date is before today.
     *
     * @return a list of schedules with next date before today
     */
    @Override
    public List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday() {
        return recurringScheduleDAO.findActiveSchedulesWithNextDateBeforeToday();
    }

    /**
     * Retrieves schedules associated with a specific customer.
     *
     * @param customer the customer to filter schedules
     * @return a list of schedules for the customer
     */
    @Override
    public List<RecurringSchedule> findSchedulesByCustomer(final Customer customer) {
        return recurringScheduleDAO.findSchedulesByCustomer(customer);
    }
}
