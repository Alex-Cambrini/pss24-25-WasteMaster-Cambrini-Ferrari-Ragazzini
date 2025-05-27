package it.unibo.wastemaster.core.dao;

import java.util.List;
import java.time.LocalDate;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * DAO for {@link RecurringSchedule} entities.
 */
public final class RecurringScheduleDAO extends GenericDAO<RecurringSchedule> {

    /**
     * Constructs a new RecurringScheduleDAO.
     *
     * @param entityManager the entity manager to use
     */
    public RecurringScheduleDAO(final EntityManager entityManager) {
        super(entityManager, RecurringSchedule.class);
    }

    /**
     * Finds active schedules that have no future collections.
     *
     * @return list of matching RecurringSchedule entities
     */
    public List<RecurringSchedule> findActiveSchedulesWithoutFutureCollections() {
        LocalDate currentDate = LocalDate.now();
        String jpql = """
                SELECT rs FROM RecurringSchedule rs
                WHERE rs.nextCollectionDate > :currentDate
                AND rs.status = 'ACTIVE' AND NOT EXISTS (
                    SELECT 1 FROM Collection c
                    WHERE c.schedule = rs AND c.date > :currentDate )
                """;
        return entityManager.createQuery(jpql, RecurringSchedule.class)
                .setParameter("currentDate", currentDate).getResultList();
    }

    /**
     * Finds active schedules with next collection date before today.
     *
     * @return list of matching RecurringSchedule entities
     */
    public List<RecurringSchedule> findActiveSchedulesWithNextDateBeforeToday() {
        LocalDate currentDate = LocalDate.now();
        String jpql = """
                SELECT rs FROM RecurringSchedule rs
                WHERE rs.status = 'ACTIVE'
                AND rs.nextCollectionDate < :currentDate
                """;
        return entityManager.createQuery(jpql, RecurringSchedule.class)
                .setParameter("currentDate", currentDate).getResultList();
    }

    /**
     * Finds schedules by customer.
     *
     * @param customer the customer to search by
     * @return list of matching RecurringSchedule entities
     */
    public List<RecurringSchedule> findSchedulesByCustomer(final Customer customer) {
        String jpql = "SELECT s FROM Schedule s WHERE s.customer = :customer";
        TypedQuery<RecurringSchedule> query =
                entityManager.createQuery(jpql, RecurringSchedule.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }
}
