package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import jakarta.persistence.EntityManager;

public class RecurringScheduleDAO extends GenericDAO<RecurringSchedule> {

    public RecurringScheduleDAO(EntityManager entityManager) {
        super(entityManager, RecurringSchedule.class);
    }

    public List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections() {
        String jpql = "SELECT rs FROM RecurringSchedule rs " +
        "WHERE rs.nextCollectionDate > CURRENT_DATE " +
        "AND rs.status = 'ACTIVE' " +
        "AND NOT EXISTS (" +
        "SELECT 1 FROM Collection c " +
        "WHERE c.schedule = rs AND c.date > CURRENT_DATE" +
        ")";
        return entityManager.createQuery(jpql, RecurringSchedule.class).getResultList();
    }

    public List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday() {
        String jpql = "SELECT rs FROM RecurringSchedule rs " +
                "WHERE rs.status = 'ACTIVE' " +
                "AND rs.nextCollectionDate < CURRENT_DATE";
        return entityManager.createQuery(jpql, RecurringSchedule.class).getResultList();
    }
}