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
 * DAO class for managing {@link Trip} entities.
 * <p>
 * This class is {@code final} and not intended for extension.
 * It provides read-oriented queries in addition to the basic CRUD from
 * {@link GenericDAO}.
 */
public final class TripDAO extends GenericDAO<Trip> {

  /** Limit used for fetching the most recently modified trips. */
  private static final int LAST_MODIFIED_LIMIT = 5;

  /**
   * Constructs a TripDAO with the given EntityManager.
   *
   * @param entityManager the EntityManager instance to use
   */
  public TripDAO(final EntityManager entityManager) {
    super(entityManager, Trip.class);
  }

  /**
   * Finds vehicles available for a prospective trip time window.
   * A vehicle is available if it is in service, not due for maintenance
   * during the trip, and not already allocated to an overlapping active trip.
   *
   * @param start the prospective trip start (inclusive)
   * @param end   the prospective trip end (exclusive)
   * @return list of available vehicles
   */
  public List<Vehicle> findAvailableVehicles(
      final LocalDateTime start, final LocalDateTime end) {
    final String jpql = """
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

  /**
   * Retrieves trips where the specified employee is assigned as an operator.
   *
   * @param operator the employee to filter by
   * @return list of trips operated by the given employee
   */
  public List<Trip> findByOperator(final Employee operator) {
    final String jpql = "SELECT t FROM Trip t JOIN t.operators o WHERE o = :operator";
    return getEntityManager().createQuery(jpql, Trip.class)
        .setParameter("operator", operator)
        .getResultList();
  }

  /**
   * Finds employees with role {@link Employee.Role#OPERATOR} who are free
   * in the given time window (i.e., not assigned to an overlapping active trip).
   *
   * @param start start of the window (inclusive)
   * @param end   end of the window (exclusive)
   * @return list of available operators
   */
  public List<Employee> findAvailableOperators(
      final LocalDateTime start, final LocalDateTime end) {
    final String jpql = """
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

  /**
   * Like {@link #findAvailableOperators(LocalDateTime, LocalDateTime)} but
   * excludes
   * the current trip from overlap checks to allow editing its own assignments.
   *
   * @param start      start of the window (inclusive)
   * @param end        end of the window (exclusive)
   * @param tripToEdit the trip being edited (excluded from overlap)
   * @return list of available operators
   */
  public List<Employee> findAvailableOperatorsForEdit(
      final LocalDateTime start,
      final LocalDateTime end,
      final Trip tripToEdit) {
    final String jpql = """
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

  /**
   * Returns the distinct postal codes that have at least one
   * {@link it.unibo.wastemaster.domain.model.Collection} on the given date
   * not yet assigned to any trip and with status {@link CollectionStatus#ACTIVE}.
   *
   * @param date the target date
   * @return distinct postal codes to be scheduled
   */
  public List<String> findAvailablePostalCodes(final LocalDate date) {
    final String jpql = """
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

  /**
   * Fetches the most recently modified trips, ordered by {@code lastModified}
   * descending.
   *
   * @return up to {@link #LAST_MODIFIED_LIMIT} most recently modified trips
   */
  public List<Trip> findLast5Modified() {
    return getEntityManager().createQuery(
        "SELECT t FROM Trip t ORDER BY t.lastModified DESC", Trip.class)
        .setMaxResults(LAST_MODIFIED_LIMIT)
        .getResultList();
  }

  /**
   * Counts trips with status {@link Trip.TripStatus#COMPLETED}.
   *
   * @return the number of completed trips
   */
  public int countCompleted() {
    final String jpql = "SELECT COUNT(t) FROM Trip t WHERE t.status = :status";
    return getEntityManager().createQuery(jpql, Long.class)
        .setParameter("status", Trip.TripStatus.COMPLETED)
        .getSingleResult()
        .intValue();
  }
}
