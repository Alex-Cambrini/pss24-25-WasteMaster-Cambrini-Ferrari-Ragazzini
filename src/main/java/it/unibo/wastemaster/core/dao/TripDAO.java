package it.unibo.wastemaster.core.dao;


import java.util.List;

import it.unibo.wastemaster.core.models.Trip;
import jakarta.persistence.EntityManager;

public class TripDAO extends GenericDAO<Trip> {

    public TripDAO(EntityManager entityManager) {
        super(entityManager, Trip.class);
    }

   
   public List<Trip> findByStatus(Trip.TripStatus status) {
    return entityManager.createQuery(
        "SELECT t FROM Trip t WHERE t.status = :status", Trip.class)
        .setParameter("status", status)
        .getResultList();
}
}

