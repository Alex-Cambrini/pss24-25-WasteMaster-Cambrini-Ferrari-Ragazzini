package it.unibo.wastemaster.core.dao;


import java.time.LocalDateTime;
import java.util.List;

import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.utils.TransactionHelper;
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

  
    public List<Trip> findByPostalCode(String postalCode) {
        return entityManager.createQuery(
                "SELECT t FROM Trip t WHERE t.postalCode = :postalCode", Trip.class)
                .setParameter("postalCode", postalCode)
                .getResultList();  
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
    
    public List<Trip> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    return entityManager.createQuery("SELECT t FROM Trip t WHERE t.departureTime BETWEEN :startDate AND :endDate", Trip.class)
                        .setParameter("startDate", startDate)
                        .setParameter("endDate", endDate)
                        .getResultList();
    }


    public long countByStatus(Trip.TripStatus status) {
        return entityManager.createQuery("SELECT COUNT(t) FROM Trip t WHERE t.status = :status", Long.class)
                            .setParameter("status", status)
                            .getSingleResult();
    }
   
    public void insert(Trip trip) {
    TransactionHelper.executeTransaction(entityManager, () -> entityManager.merge(trip));
    }   
}

