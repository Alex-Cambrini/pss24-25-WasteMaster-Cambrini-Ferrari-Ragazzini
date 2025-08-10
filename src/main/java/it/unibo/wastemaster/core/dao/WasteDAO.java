package it.unibo.wastemaster.core.dao;

import jakarta.persistence.EntityManager;
import java.util.List;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.dao.GenericDAO;

/**
 * DAO for managing Waste entities.
 */
public final class WasteDAO extends GenericDAO<Waste> {

    /**
     * Constructs a WasteDAO with the given entity manager.
     *
     * @param entityManager the EntityManager to use
     */
    public WasteDAO(final EntityManager entityManager) {
        super(entityManager, Waste.class);
    }

    /**
     * Checks whether a non-deleted waste with the given name exists.
     *
     * @param name the name of the waste
     * @return true if a non-deleted waste with that name exists, false otherwise
     */
    public boolean existsByName(final String name) {
        return getEntityManager().createQuery(
                "SELECT COUNT(w) FROM Waste w WHERE w.name = :name AND w.deleted = false",
                Long.class).setParameter("name", name).getSingleResult() > 0;
    }

    /**
     * Retrieves all non-deleted wastes.
     *
     * @return list of active (non-deleted) wastes
     */
    public List<Waste> findActiveWastes() {
        return getEntityManager().createQuery("""
                    SELECT w FROM Waste w
                    WHERE w.deleted = false
                """, Waste.class).getResultList();
    }
}
