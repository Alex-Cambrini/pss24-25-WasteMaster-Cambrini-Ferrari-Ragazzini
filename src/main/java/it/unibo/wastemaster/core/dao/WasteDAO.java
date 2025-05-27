package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Waste;
import jakarta.persistence.EntityManager;

public class WasteDAO extends GenericDAO<Waste> {

    public WasteDAO(EntityManager entityManager) {
        super(entityManager, Waste.class);
    }

    public boolean existsByName(String name) {
        return entityManager
                .createQuery("SELECT COUNT(w) FROM Waste w WHERE w.name = :name AND w.deleted = false", Long.class)
                .setParameter("name", name)
                .getSingleResult() > 0;
    }

    public List<Waste> findActiveWastes() {
        return entityManager.createQuery("""
                	SELECT w FROM Waste w
                	WHERE w.deleted = false
                """, Waste.class).getResultList();
    }
}
