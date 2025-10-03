package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing RecurringSchedule entities.
 * Provides CRUD operations and retrieval methods for active schedules and
 * customer-specific schedules.
 */
public interface RecurringScheduleRepository {

    /**
     * Persists a new recurring schedule.
     *
     * @param schedule the RecurringSchedule entity to save
     */
    void save(RecurringSchedule schedule);

    /**
     * Updates an existing recurring schedule.
     *
     * @param schedule the RecurringSchedule entity to update
     */
    void update(RecurringSchedule schedule);

    /**
     * Retrieves a recurring schedule by its unique ID.
     *
     * @param id the unique identifier of the recurring schedule
     * @return an Optional containing the RecurringSchedule if found, or empty if not
     * found
     */
    Optional<RecurringSchedule> findById(Integer id);

    /**
     * Retrieves active schedules that do not have any future collections assigned.
     *
     * @return a list of active RecurringSchedule entities without future collections
     */
    List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections();

    /**
     * Retrieves active schedules whose next scheduled date is before today.
     *
     * @return a list of active RecurringSchedule entities with next date before today
     */
    List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday();

    /**
     * Retrieves all schedules associated with a specific customer.
     *
     * @param customer the Customer entity to filter schedules
     * @return a list of RecurringSchedule entities for the given customer
     */
    List<RecurringSchedule> findSchedulesByCustomer(Customer customer);
}
