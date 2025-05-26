package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;

public class CollectionDAO extends GenericDAO<Collection> {

    public CollectionDAO(EntityManager entityManager) {
        super(entityManager, Collection.class);
    }

    public List<Collection> findAllCollectionsBySchedule(Schedule schedule) {
        return entityManager.createQuery(
                "SELECT c FROM Collection c WHERE c.schedule = :schedule",
                Collection.class)
                .setParameter("schedule", schedule)
                .getResultList();
    }

    public List<Collection> findCollectionByStatus(Collection.CollectionStatus status) {
        return entityManager
                .createQuery("SELECT c FROM Collection c WHERE c.collectionStatus = :status", Collection.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Collection findActiveCollectionByRecurringSchedule(RecurringSchedule schedule) {
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
}