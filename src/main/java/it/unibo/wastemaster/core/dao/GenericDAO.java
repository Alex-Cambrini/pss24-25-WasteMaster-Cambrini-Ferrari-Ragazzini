package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.utils.TransactionHelper;
import jakarta.persistence.EntityManager;
import java.util.List;

public class GenericDAO<T> {
    protected final EntityManager entityManager;
    private final Class<T> entityClass;

    public GenericDAO(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public void insert(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> entityManager.persist(entity));
    }

    public void update(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> entityManager.merge(entity));
    }

    public void delete(T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> {
            T attached = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(attached);
        });
    }

    public T findById(int id) {
        return entityManager.find(entityClass, id);
    }

    public List<T> findAll() {
        return entityManager.createQuery("FROM " + entityClass.getName(), entityClass)
                            .getResultList();
    }
}
