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

    public List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end) {
        String jpql = """
            SELECT v FROM Vehicle v
            WHERE v.vehicleStatus = :inService
            AND v.nextMaintenanceDate > :tripEnd
            AND NOT EXISTS (
                SELECT 1 FROM Trip t
                WHERE t.assignedVehicle = v
                    AND t.departureTime < :tripEnd
                    AND t.expectedReturnTime > :tripStart
            )
            """;

        return getEntityManager().createQuery(jpql, Vehicle.class)
                .setParameter("inService", Vehicle.VehicleStatus.IN_SERVICE)
                .setParameter("tripStart", start)
                .setParameter("tripEnd", end)
                .getResultList();
    }

    public List<Employee> findAvailableOperators(LocalDateTime start, LocalDateTime end) {
        String jpql = """
            SELECT e FROM Employee e
            WHERE NOT EXISTS (
                SELECT 1 FROM Trip t JOIN t.operators o
                WHERE o = e
                AND t.departureTime < :tripEnd
                AND t.expectedReturnTime > :tripStart
            )
        """;

        return getEntityManager().createQuery(jpql, Employee.class)
                .setParameter("tripStart", start)
                .setParameter("tripEnd", end)
                .getResultList();
    }
}
