package it.unibo.wastemaster.core.dao;


import it.unibo.wastemaster.core.models.Trip;
import jakarta.persistence.EntityManager;

public class TripDAO extends GenericDAO<Trip> {

    public TripDAO(EntityManager entityManager) {
        super(entityManager, Trip.class);
    }

 
}

