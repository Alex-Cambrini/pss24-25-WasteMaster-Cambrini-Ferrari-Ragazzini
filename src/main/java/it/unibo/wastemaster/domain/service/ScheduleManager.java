package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.ScheduleRepository;
import java.util.List;

/**
 * Service class responsible for managing schedule entities.
 */
public class ScheduleManager {

    private final ScheduleRepository scheduleRepository;

    /**
     * Constructs a ScheduleManager with the given ScheduleRepository.
     *
     * @param scheduleRepository the repository used for schedule persistence operations
     */
    public ScheduleManager(final ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Retrieves all schedules in the system.
     *
     * @return a list of all schedules
     */
    public List<Schedule> findAllSchedule() {
        return scheduleRepository.findAll();
    }
}
