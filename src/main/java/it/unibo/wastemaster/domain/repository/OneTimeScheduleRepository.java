package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import java.util.Optional;

/**
 * Repository interface for managing OneTimeSchedule entities.
 * Provides basic CRUD operations.
 */
public interface OneTimeScheduleRepository {

    /**
     * Persists a new one-time schedule.
     *
     * @param schedule the OneTimeSchedule entity to save
     */
    void save(OneTimeSchedule schedule);

    /**
     * Updates an existing one-time schedule.
     *
     * @param schedule the OneTimeSchedule entity to update
     */
    void update(OneTimeSchedule schedule);

    /**
     * Retrieves a one-time schedule by its unique ID.
     *
     * @param id the unique identifier of the one-time schedule
     * @return an Optional containing the OneTimeSchedule if found, or empty if not found
     */
    Optional<OneTimeSchedule> findById(Integer id);
}
