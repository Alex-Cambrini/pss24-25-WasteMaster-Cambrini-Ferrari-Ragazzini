package it.unibo.wastemaster.core.dao;

import java.util.List;
import java.sql.Date;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class RecurringScheduleDAO extends GenericDAO<RecurringSchedule> {

    public RecurringScheduleDAO(EntityManager entityManager) {
        super(entityManager, RecurringSchedule.class);
    }

    public List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections() {
        String jpql = "SELECT rs FROM RecurringSchedule rs " +
                "WHERE rs.nextCollectionDate > :currentDate " +
                "AND rs.status = 'ACTIVE' " +
                "AND NOT EXISTS (" +
                "SELECT 1 FROM Collection c " +
                "WHERE c.schedule = rs AND c.date > :currentDate" +
                ")";
        return entityManager.createQuery(jpql, RecurringSchedule.class)
                            .setParameter("currentDate", new Date(DateUtils.getCurrentDate().getTime()))
                            .getResultList();
    }

    public List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday() {
        String jpql = "SELECT rs FROM RecurringSchedule rs " +
                "WHERE rs.status = 'ACTIVE' " +
                "AND rs.nextCollectionDate < :currentDate";
        return entityManager.createQuery(jpql, RecurringSchedule.class)
                            .setParameter("currentDate", new Date(DateUtils.getCurrentDate().getTime()))
                            .getResultList();
    }

    public List<RecurringSchedule> findScheduleByCustomer(Customer customer) {
        String jpql = "SELECT s FROM Schedule s WHERE s.customer = :customer";
        TypedQuery<RecurringSchedule> query = entityManager.createQuery(jpql, RecurringSchedule.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }
}
