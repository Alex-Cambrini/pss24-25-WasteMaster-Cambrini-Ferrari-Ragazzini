package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end) {
        String jpql = """
                SELECT v FROM Vehicle v
                WHERE v.vehicleStatus = :inService
                  AND (v.nextMaintenanceDate IS NULL OR v.nextMaintenanceDate > :tripEndDate)
                  AND NOT EXISTS (
                      SELECT 1 FROM Trip t
                      WHERE t.assignedVehicle = v
                        AND t.status = :active
                        AND t.departureTime < :tripEnd
                        AND t.expectedReturnTime > :tripStart
                  )
                """;

        return getEntityManager().createQuery(jpql, Vehicle.class)
                .setParameter("inService", Vehicle.VehicleStatus.IN_SERVICE)
                .setParameter("active", Trip.TripStatus.ACTIVE)
                .setParameter("tripStart", start)
                .setParameter("tripEnd", end)
                .setParameter("tripEndDate", end.toLocalDate())
                .getResultList();
    }

    public List<Trip> findByOperator(Employee operator) {
        String jpql = "SELECT t FROM Trip t JOIN t.operators o WHERE o = :operator";
        return getEntityManager().createQuery(jpql, Trip.class)
                .setParameter("operator", operator)
                .getResultList();
    }

    public List<Employee> findAvailableOperators(LocalDateTime start, LocalDateTime end) {
        String jpql = """
                SELECT e FROM Employee e
                WHERE e.role = :operatorRole
                  AND NOT EXISTS (
                      SELECT 1 FROM Trip t JOIN t.operators o
                      WHERE o = e
                        AND t.status = :active
                        AND t.departureTime < :tripEnd
                        AND t.expectedReturnTime > :tripStart
                  )
                """;

        return getEntityManager().createQuery(jpql, Employee.class)
                .setParameter("active", Trip.TripStatus.ACTIVE)
                .setParameter("tripStart", start)
                .setParameter("tripEnd", end)
                .setParameter("operatorRole", Employee.Role.OPERATOR)
                .getResultList();
    }

    public List<Employee> findAvailableOperatorsForEdit(LocalDateTime start,
                                                        LocalDateTime end,
                                                        Trip tripToEdit) {
        String jpql = """
            SELECT e FROM Employee e
            WHERE e.role = :operatorRole
              AND NOT EXISTS (
                  SELECT 1 FROM Trip t JOIN t.operators o
                  WHERE o = e
                    AND t.status = :active
                    AND t.departureTime < :tripEnd
                    AND t.expectedReturnTime > :tripStart
                    AND t.tripId <> :currentTripId
              )
            """;

        return getEntityManager().createQuery(jpql, Employee.class)
                .setParameter("active", Trip.TripStatus.ACTIVE)
                .setParameter("tripStart", start)
                .setParameter("tripEnd", end)
                .setParameter("operatorRole", Employee.Role.OPERATOR)
                .setParameter("currentTripId", tripToEdit.getTripId())
                .getResultList();
    }


    public List<String> findAvailablePostalCodes(LocalDate date) {
        String jpql = """
                SELECT DISTINCT loc.postalCode
                FROM Collection c
                JOIN c.schedule s
                JOIN s.customer cust
                JOIN cust.location loc
                WHERE c.date = :date
                  AND c.trip IS NULL
                  AND c.collectionStatus = :toBeScheduled
                """;

        return getEntityManager().createQuery(jpql, String.class)
                .setParameter("date", date)
                .setParameter("toBeScheduled", CollectionStatus.ACTIVE)
                .getResultList();
    }
}
