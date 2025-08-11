package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Trip;
import jakarta.persistence.EntityManager;

/**
 * DAO class for managing Trip entities.
 * Extends GenericDAO to provide basic CRUD operations.
 */
public class TripDAO extends GenericDAO<Trip> {

    /**
     * Constructs a TripDAO with the given EntityManager.
     *
     * @param entityManager the EntityManager instance to use
     */
    public TripDAO(final EntityManager entityManager) {
        super(entityManager, Trip.class);
    }
}
