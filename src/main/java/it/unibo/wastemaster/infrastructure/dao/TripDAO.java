package it.unibo.wastemaster.infrastructure.dao;

import java.time.LocalDateTime;
import java.util.List;




import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
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

     /**
     * Finds all collections by postal code (CAP).
     *
     * @param postalCode the postal code to filter collections
     * @return a list of collections associated with the given postal code
     */
    
       
    public List<Collection> findCollectionsByPostalCode(String postalCode) {
        String jpql = "SELECT c FROM Trip t JOIN t.collections c WHERE t.postalCode = :postalCode";
        return getEntityManager().createQuery(jpql, Collection.class)
                .setParameter("postalCode", postalCode)
                .getResultList();
    }

    public List<Trip> findTripsByVehicleAndPeriod(Vehicle vehicle, LocalDateTime start, LocalDateTime end) {
        String jpql = "SELECT t FROM Trip t WHERE t.assignedVehicle = :vehicle "
                    + "AND t.departureTime < :end AND t.expectedReturnTime > :start";
        return getEntityManager().createQuery(jpql, Trip.class)
                .setParameter("vehicle", vehicle)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<Trip> findTripsByOperatorAndPeriod(Employee operator, LocalDateTime start, LocalDateTime end) {
        String jpql = "SELECT t FROM Trip t JOIN t.operators o WHERE o = :operator "
                    + "AND t.departureTime < :end AND t.expectedReturnTime > :start";
        return getEntityManager().createQuery(jpql, Trip.class)
                .setParameter("operator", operator)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
