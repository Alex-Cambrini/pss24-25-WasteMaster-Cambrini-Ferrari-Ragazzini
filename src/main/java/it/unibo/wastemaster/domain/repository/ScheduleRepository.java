package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Schedule;
import java.util.List;

/**
 * Repository interface for managing Schedule entities.
 * Provides basic retrieval operations.
 */
public interface ScheduleRepository {

    /**
     * Retrieves all schedules.
     *
     * @return a list of all Schedule entities
     */
    List<Schedule> findAll();
}
