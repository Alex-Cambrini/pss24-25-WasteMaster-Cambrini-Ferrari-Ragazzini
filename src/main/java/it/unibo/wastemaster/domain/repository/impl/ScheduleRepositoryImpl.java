package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.ScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.ScheduleDAO;
import java.util.List;

/**
 * Implementation of {@link ScheduleRepository} that uses {@link ScheduleDAO}
 * to perform CRUD operations on Schedule entities.
 */
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleDAO scheduleDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param scheduleDAO the DAO used to access schedule data
     */
    public ScheduleRepositoryImpl(final ScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;
    }

    /**
     * Retrieves all schedules.
     *
     * @return a list of all schedules
     */
    @Override
    public List<Schedule> findAll() {
        return scheduleDAO.findAll();
    }
}
