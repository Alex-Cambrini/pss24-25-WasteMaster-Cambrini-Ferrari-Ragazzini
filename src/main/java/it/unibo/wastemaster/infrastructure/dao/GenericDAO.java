package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.infrastructure.utils.TransactionHelper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * GenericDAO class for managing entities.
 *
 * @param <T> the entity type
 */
public class GenericDAO<T> {

    private final EntityManager entityManager;
    private final Class<T> entityClass;

    /**
     * Constructs a GenericDAO.
     *
     * @param entityManager the entity manager (final)
     * @param entityClass the entity class (final)
     */
    public GenericDAO(final EntityManager entityManager, final Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    /**
     * Gets the entity manager.
     *
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Gets the entity class.
     *
     * @return the entity class
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * Inserts the given entity within a transaction.
     *
     * @param entity the entity to insert (final)
     */
    public void insert(final T entity) {
        TransactionHelper.executeTransaction(entityManager,
                () -> entityManager.persist(entity));
    }

    /**
     * Updates the given entity within a transaction.
     *
     * @param entity the entity to update (final)
     */
    public void update(final T entity) {
        TransactionHelper.executeTransaction(entityManager,
                () -> entityManager.merge(entity));
    }

    /**
     * Deletes the given entity within a transaction.
     *
     * @param entity the entity to delete (final)
     */
    public void delete(final T entity) {
        TransactionHelper.executeTransaction(entityManager, () -> {
            T attached =
                    entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(attached);
        });
    }

    /**
     * Finds an entity by its id.
     *
     * @param id the entity id (final)
     * @return the found entity or null
     */
    public Optional<T> findById(final int id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }
    /**
     * Finds all entities of the given type.
     *
     * @return list of all entities
     */
    public List<T> findAll() {
        return entityManager.createQuery("FROM " + entityClass.getName(), entityClass)
                .getResultList();
    }
}
