package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO for {@link Collection} entity operations.
 */
public class CollectionDAO extends GenericDAO<Collection> {

    /**
     * Constructs a new CollectionDAO.
     *
     * @param entityManager the entity manager to use
     */
    public CollectionDAO(final EntityManager entityManager) {
        super(entityManager, Collection.class);
    }

    /**
     * Retrieves all collections associated with the given {@link Schedule}.
     *
     * @param schedule the schedule to filter collections by
     * @return list of collections matching the schedule
     */
    public final List<Collection> findAllCollectionsBySchedule(final Schedule schedule) {
        return getEntityManager()
                .createQuery("SELECT c FROM Collection c WHERE c.schedule = :schedule",
                        Collection.class)
                .setParameter("schedule", schedule).getResultList();
    }

    /**
     * Retrieves all collections with the specified
     * {@link Collection.CollectionStatus}.
     *
     * @param status the collection status to filter by
     * @return list of collections matching the status
     */
    public final List<Collection> findCollectionByStatus(
            final Collection.CollectionStatus status) {
        return getEntityManager().createQuery(
                "SELECT c FROM Collection c WHERE c.collectionStatus = :status",
                Collection.class).setParameter("status", status).getResultList();
    }

    /**
     * Finds an active {@link Collection} linked to the given
     * {@link RecurringSchedule}.
     * An active collection is one whose status is not CANCELLED.
     *
     * @param schedule the recurring schedule to search collections for
     * @return the active collection if found, otherwise null
     */
    public final Collection findActiveCollectionByRecurringSchedule(
            final RecurringSchedule schedule) {
        try {
            String jpql = """
                        SELECT c FROM Collection c
                        WHERE c.schedule = :schedule
                        AND c.collectionStatus != :cancelledStatus
                    """;
            return getEntityManager().createQuery(jpql, Collection.class)
                    .setParameter("schedule", schedule).setParameter("cancelledStatus",
                            Collection.CollectionStatus.CANCELLED)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Retrieves a list of Collection entities whose date field is between the
     * specified
     * start and end dates (inclusive).
     *
     * @param start the start date of the range (inclusive)
     * @param end   the end date of the range (inclusive)
     * @return a list of Collection entities with dates between start and end
     */
    public List<Collection> findByDateRange(final LocalDate start, final LocalDate end) {
        return getEntityManager().createQuery("""
                SELECT c FROM Collection c
                WHERE c.date BETWEEN :start AND :end
                """, Collection.class).setParameter("start", start)
                .setParameter("end", end).getResultList();
    }

    public List<Collection> findCollectionsByPostalCodeAndDate(final String postalCode, final LocalDate date) {
        final String jpql = """
                SELECT c
                FROM Collection c
                JOIN c.schedule s
                JOIN s.customer cust
                JOIN cust.location loc
                WHERE loc.postalCode = :postal
                  AND c.date = :date
                  AND c.collectionStatus = :status
                """;

        return getEntityManager().createQuery(jpql, Collection.class)
                .setParameter("postal", postalCode)
                .setParameter("date", date)
                .setParameter("status", Collection.CollectionStatus.ACTIVE)
                .getResultList();
    }

    /**
     * Retrieves all completed collections that have not been billed for the given
     * customer.
     *
     * @param customer the customer to filter collections by
     * @return list of completed and not yet billed collections for the customer
     */
    public List<Collection> findCompletedNotBilledByCustomer(final Customer customer) {
        final String jpql = """
                SELECT c
                FROM Collection c
                WHERE c.customer = :customer
                  AND c.collectionStatus = :completedStatus
                  AND c.isBilled = false
                """;

        return getEntityManager().createQuery(jpql, Collection.class)
                .setParameter("customer", customer)
                .setParameter("completedStatus", Collection.CollectionStatus.COMPLETED)
                .getResultList();
    }

}
