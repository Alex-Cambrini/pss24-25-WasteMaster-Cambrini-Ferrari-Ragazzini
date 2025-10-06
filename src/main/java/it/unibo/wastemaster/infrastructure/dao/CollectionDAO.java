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
public final class CollectionDAO extends GenericDAO<Collection> {

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
    public List<Collection> findAllCollectionsBySchedule(final Schedule schedule) {
        final String jpql = """
                SELECT c
                FROM Collection c
                WHERE c.schedule = :schedule
                """;

        return getEntityManager()
                .createQuery(jpql, Collection.class)
                .setParameter("schedule", schedule)
                .getResultList();
    }

    /**
     * Retrieves all collections with the specified
     * {@link Collection.CollectionStatus}.
     *
     * @param status the collection status to filter by
     * @return list of collections matching the status
     */
    public List<Collection> findCollectionByStatus(
            final Collection.CollectionStatus status) {
        return getEntityManager()
                .createQuery(
                        """
                                SELECT c FROM Collection c
                                WHERE c.collectionStatus = :status
                                """,
                        Collection.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Finds an active {@link Collection} linked to the given
     * {@link RecurringSchedule}. An active collection is one whose
     * status is not CANCELLED.
     *
     * @param schedule the recurring schedule to search collections for
     * @return the active collection if found, otherwise null
     */
    public Collection findActiveCollectionByRecurringSchedule(
            final RecurringSchedule schedule) {
        try {
            String jpql = """
                    SELECT c FROM Collection c
                    WHERE c.schedule = :schedule
                    AND c.collectionStatus != :cancelledStatus
                    """;
            return getEntityManager()
                    .createQuery(jpql, Collection.class)
                    .setParameter("schedule", schedule)
                    .setParameter(
                            "cancelledStatus",
                            Collection.CollectionStatus.CANCELLED)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retrieves collections taking place on a specific {@code date} for
     * customers whose location has the given {@code postalCode}.
     * Results include only collections currently in
     * {@link Collection.CollectionStatus#ACTIVE} status.
     * <p>
     * Extension note: this DAO is {@code final} and not intended for
     * subclassing. If different filtering strategies are needed, prefer
     * creating a separate repository/DAO or adding new query methods here.
     *
     * @param postalCode the customer's location postal code to match
     * @param date the collection date to match
     * @return list of matching {@link Collection} entities
     */
    public List<Collection> findCollectionsByPostalCodeAndDate(
            final String postalCode,
            final LocalDate date) {
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

        return getEntityManager()
                .createQuery(jpql, Collection.class)
                .setParameter("postal", postalCode)
                .setParameter("date", date)
                .setParameter(
                        "status",
                        Collection.CollectionStatus.ACTIVE)
                .getResultList();
    }

    /**
     * Retrieves all completed collections that have not yet been billed
     * for the specified customer.
     *
     * @param customer the customer whose collections are to be retrieved
     * @return a list of completed and not yet billed collections for the
     * given customer
     */
    public List<Collection> findCompletedNotBilledByCustomer(
            final Customer customer) {
        final String jpql = """
                SELECT c
                FROM Collection c
                WHERE c.customer.id = :customerId
                  AND c.collectionStatus = :completedStatus
                  AND c.isBilled = false
                """;

        List<Collection> collections = getEntityManager()
                .createQuery(jpql, Collection.class)
                .setParameter("customerId", customer.getCustomerId())
                .setParameter(
                        "completedStatus",
                        Collection.CollectionStatus.COMPLETED)
                .getResultList();

        return collections;
    }
}
