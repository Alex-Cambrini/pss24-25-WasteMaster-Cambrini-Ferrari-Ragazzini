package it.unibo.wastemaster.domain.repository.impl;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.repository.OneTimeScheduleRepository;
import it.unibo.wastemaster.infrastructure.dao.OneTimeScheduleDAO;

public class OneTimeScheduleRepositoryImpl implements OneTimeScheduleRepository {

    private final OneTimeScheduleDAO dao;

    public OneTimeScheduleRepositoryImpl(EntityManager entityManager) {
        this.dao = new OneTimeScheduleDAO(entityManager);
    }

    @Override
    public void save(OneTimeSchedule schedule) {
        dao.insert(schedule);
    }

    @Override
    public void update(OneTimeSchedule schedule) {
        dao.update(schedule);
    }
}