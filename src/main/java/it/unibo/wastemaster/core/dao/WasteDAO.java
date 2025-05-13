package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Waste;
import jakarta.persistence.EntityManager;

public class WasteDAO extends GenericDAO<Waste> {    

    public WasteDAO(EntityManager entityManager) {
        super(entityManager, Waste.class);
    }
}
