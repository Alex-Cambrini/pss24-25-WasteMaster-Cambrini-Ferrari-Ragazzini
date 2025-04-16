package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import jakarta.persistence.EntityManager;

public class RecurringScheduleDAO extends GenericDAO<RecurringSchedule> {

    public RecurringScheduleDAO(EntityManager entityManager) {
        super(entityManager, RecurringSchedule.class);
    }

    public List<RecurringSchedule> findRecurringSchedulesWithoutFutureCollections() {
        String jpql = "SELECT rs FROM RecurringSchedule rs " +
                  "LEFT JOIN Collection c ON rs.id = c.schedule.id AND c.date > CURRENT_DATE " +
                  "WHERE c.schedule.id IS NULL " +
                  "AND rs.startDate <= CURRENT_DATE";    
        return entityManager.createQuery(jpql, RecurringSchedule.class).getResultList();
    }
    
}