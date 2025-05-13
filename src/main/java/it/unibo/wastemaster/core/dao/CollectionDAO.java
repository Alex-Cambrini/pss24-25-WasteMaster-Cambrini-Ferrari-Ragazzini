package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class CollectionDAO extends GenericDAO<Collection> {

    public CollectionDAO(EntityManager entityManager) {
        super(entityManager, Collection.class);
    }

    public List<Collection> findCollectionByStatus(Collection.CollectionStatus status) {
        return entityManager
                .createQuery("SELECT c FROM Collection c WHERE c.collectionStatus = :status", Collection.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Collection findActiveCollectionByOneTimeSchedule(OneTimeSchedule schedule) {
        try {
            return entityManager.createQuery(
                    "SELECT c FROM Collection c WHERE c.schedule = :schedule AND c.collectionStatus != :cancelledStatus",
                    Collection.class)
                    .setParameter("schedule", schedule)
                    .setParameter("cancelledStatus", Collection.CollectionStatus.CANCELLED)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Collection> findCancelledCollectionsOneTimeSchedule(OneTimeSchedule schedule) {
        return entityManager.createQuery(
                "SELECT c FROM Collection c WHERE c.schedule = :schedule AND c.collectionStatus = :cancelledStatus",
                Collection.class)
                .setParameter("schedule", schedule)
                .setParameter("cancelledStatus", Collection.CollectionStatus.CANCELLED)
                .getResultList();
    }

}