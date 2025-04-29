package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import jakarta.persistence.EntityManager;

public class OneTimeScheduleDAO extends GenericDAO<OneTimeSchedule> {

    public OneTimeScheduleDAO(EntityManager entityManager) {
        super(entityManager, OneTimeSchedule.class);
    }

    public Collection findCollectionByScheduleId(int scheduleId) {
        return entityManager.createQuery(
            "SELECT c FROM Collection c WHERE c.schedule.id = :scheduleId", Collection.class)
            .setParameter("scheduleId", scheduleId)
            .getSingleResult();
    }
}
