package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Collection;
import jakarta.persistence.EntityManager;

public class CollectionDAO extends GenericDAO<Collection> {

            public CollectionDAO(EntityManager entityManager) {
        super(entityManager, Collection.class);
    }

    public List<Collection> findCollectionByStatus(Collection.CollectionStatus status) {
        return entityManager.createQuery("SELECT c FROM Collection c WHERE c.collectionStatus = :status", Collection.class)
                .setParameter("status", status)
                .getResultList();
    }

}