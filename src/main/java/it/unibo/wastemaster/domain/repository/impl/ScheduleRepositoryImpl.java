package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.repository.ScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.ScheduleDAO;
import java.util.List;

public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleDAO scheduleDAO;

    public ScheduleRepositoryImpl(ScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleDAO.findAll();
    }
}
