package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        return entityManager
                .createQuery("SELECT c FROM Collection c WHERE c.schedule = :schedule",
                        Collection.class)
                .setParameter("schedule", schedule).getResultList();
    }

    /**
     * Retrieves all collections with the specified {@link Collection.CollectionStatus}.
     *
     * @param status the collection status to filter by
     * @return list of collections matching the status
     */
    public final List<Collection> findCollectionByStatus(
            final Collection.CollectionStatus status) {
        return entityManager.createQuery(
                "SELECT c FROM Collection c WHERE c.collectionStatus = :status",
                Collection.class).setParameter("status", status).getResultList();
    }

    /**
     * Finds an active {@link Collection} linked to the given {@link RecurringSchedule}.
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
            return entityManager.createQuery(jpql, Collection.class)
                    .setParameter("schedule", schedule).setParameter("cancelledStatus",
                            Collection.CollectionStatus.CANCELLED)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
