package it.unibo.wastemaster.domain.repository.impl;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.repository.OneTimeScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.OneTimeScheduleDAO;

public class OneTimeScheduleRepositoryImpl implements OneTimeScheduleRepository {

    private final OneTimeScheduleDAO oneTimeScheduleDAO;

    public OneTimeScheduleRepositoryImpl(OneTimeScheduleDAO oneTimeScheduleDAO) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
    }

    @Override
    public void save(OneTimeSchedule schedule) {
        oneTimeScheduleDAO.insert(schedule);
    }

    @Override
    public void update(OneTimeSchedule schedule) {
        oneTimeScheduleDAO.update(schedule);
    }
}
