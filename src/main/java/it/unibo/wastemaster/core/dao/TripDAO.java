package it.unibo.wastemaster.core.dao;


import java.util.List;

import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Vehicle;
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

    
    public Trip findByPostalCode(String postalCode) {
        return entityManager.createQuery(
            "SELECT t FROM Trip t WHERE t.postalCode = :postalCode", Trip.class)
            .setParameter("postalCode", postalCode)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    public List<Trip> findByOperator(Employee operator) {
    return entityManager.createQuery("SELECT t FROM Trip t JOIN t.operators o WHERE o = :operator", Trip.class)
                        .setParameter("operator", operator)
                        .getResultList();
    }

    public List<Trip> findByVehicle(Vehicle vehicle) {
    return entityManager.createQuery("SELECT t FROM Trip t WHERE t.assignedVehicle = :vehicle", Trip.class)
                        .setParameter("vehicle", vehicle)
                        .getResultList();
    }
    
}

