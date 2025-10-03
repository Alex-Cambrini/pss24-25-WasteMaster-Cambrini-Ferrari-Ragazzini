package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Trip entities.
 * Provides CRUD operations and methods for retrieving trips, available vehicles, and
 * qualified drivers.
 */
public interface TripRepository {

    /**
     * Persists a new trip.
     *
     * @param trip the Trip entity to save
     */
    void save(Trip trip);

    /**
     * Updates an existing trip.
     *
     * @param trip the Trip entity to update
     */
    void update(Trip trip);

    /**
     * Retrieves a trip by its unique ID.
     *
     * @param tripId the unique identifier of the trip
     * @return an Optional containing the Trip if found, or empty if not found
     */
    Optional<Trip> findById(int tripId);

    /**
     * Deletes a trip.
     *
     * @param trip the Trip entity to delete
     */
    void delete(Trip trip);

    /**
     * Retrieves all trips.
     *
     * @return a list of all Trip entities
     */
    List<Trip> findAll();

    /**
     * Retrieves all trips assigned to a specific operator.
     *
     * @param operator the Employee entity to filter trips
     * @return a list of Trip entities assigned to the operator
     */
    List<Trip> findByOperator(Employee operator);

    /**
     * Retrieves all vehicles available within the specified time range.
     *
     * @param start the start time of the range
     * @param end the end time of the range
     * @return a list of available Vehicle entities
     */
    List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves all employees qualified to drive based on allowed licences and
     * availability.
     *
     * @param start the start time of the range
     * @param end the end time of the range
     * @param allowedLicences the list of licences allowed to drive
     * @return a list of qualified Employee entities
     */
    List<Employee> findQualifiedDrivers(LocalDateTime start, LocalDateTime end,
                                        List<Licence> allowedLicences);

    /**
     * Retrieves available operators excluding a specific driver within a time range.
     *
     * @param start the start time of the range
     * @param end the end time of the range
     * @param driver the Employee to exclude
     * @return a list of available Employee entities excluding the specified driver
     */
    List<Employee> findAvailableOperatorsExcludeDriver(LocalDateTime start,
                                                       LocalDateTime end,
                                                       Employee driver);

    /**
     * Retrieves qualified drivers for editing a trip, considering the trip being edited.
     *
     * @param start the start time of the range
     * @param end the end time of the range
     * @param allowedLicences the list of licences allowed to drive
     * @param tripToEdit the Trip entity being edited
     * @return a list of qualified Employee entities for editing the trip
     */
    List<Employee> findQualifiedDriversToEdit(LocalDateTime start, LocalDateTime end,
                                              List<Licence> allowedLicences,
                                              Trip tripToEdit);

    /**
     * Retrieves available operators excluding a specific driver when editing a trip.
     *
     * @param start the start time of the range
     * @param end the end time of the range
     * @param driver the Employee to exclude
     * @param tripToEdit the Trip entity being edited
     * @return a list of available Employee entities excluding the specified driver
     */
    List<Employee> findAvailableOperatorsExcludeDriverToEdit(LocalDateTime start,
                                                             LocalDateTime end,
                                                             Employee driver,
                                                             Trip tripToEdit);

    /**
     * Retrieves postal codes with available trips on the specified date.
     *
     * @param date the date to filter trips
     * @return a list of postal codes with available trips
     */
    List<String> findAvailablePostalCodes(LocalDate date);

    /**
     * Retrieves the last 5 modified trips for notification purposes.
     *
     * @return a list of the 5 most recently modified Trip entities
     */
    List<Trip> findLast5Modified();

    /**
     * Counts the number of completed trips.
     *
     * @return the total number of completed trips
     */
    int countCompleted();
}
