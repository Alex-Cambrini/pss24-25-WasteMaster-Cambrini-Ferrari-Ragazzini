package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.dao.TripDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TripRepository} that uses {@link TripDAO}
 * to perform CRUD operations on Trip entities and manage related resources.
 */
public class TripRepositoryImpl implements TripRepository {

    private final TripDAO tripDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param tripDAO the DAO used to access trip data
     */
    public TripRepositoryImpl(final TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    /**
     * Persists a new trip.
     *
     * @param trip the trip to save
     */
    @Override
    public void save(final Trip trip) {
        tripDAO.insert(trip);
    }

    /**
     * Updates an existing trip.
     *
     * @param trip the trip to update
     */
    @Override
    public void update(final Trip trip) {
        tripDAO.update(trip);
    }

    /**
     * Retrieves a trip by its ID.
     *
     * @param tripId the trip ID
     * @return an Optional containing the trip if found, or empty
     */
    @Override
    public Optional<Trip> findById(final int tripId) {
        return tripDAO.findById(tripId);
    }

    /**
     * Deletes a trip.
     *
     * @param trip the trip to delete
     */
    @Override
    public void delete(final Trip trip) {
        tripDAO.delete(trip);
    }

    /**
     * Retrieves all trips.
     *
     * @return a list of all trips
     */
    @Override
    public List<Trip> findAll() {
        return tripDAO.findAll();
    }

    /**
     * Retrieves trips assigned to a specific operator.
     *
     * @param operator the operator to filter trips
     * @return a list of trips for the operator
     */
    @Override
    public List<Trip> findByOperator(final Employee operator) {
        return tripDAO.findByOperator(operator);
    }

    /**
     * Retrieves vehicles available within a time range.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @return a list of available vehicles
     */
    @Override
    public List<Vehicle> findAvailableVehicles(final LocalDateTime start,
                                               final LocalDateTime end) {
        return tripDAO.findAvailableVehicles(start, end);
    }

    /**
     * Retrieves employees available in a time range and qualified with allowed licences.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @param allowedLicences the licences to filter employees
     * @return a list of qualified employees
     */
    @Override
    public List<Employee> findQualifiedDrivers(final LocalDateTime start,
                                               final LocalDateTime end,
                                               final List<Licence> allowedLicences) {
        return tripDAO.findAvailableOperators(start, end).stream()
                .filter(e -> allowedLicences.contains(e.getLicence()))
                .toList();
    }

    /**
     * Retrieves available employees excluding a specific driver.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @param driver the employee to exclude
     * @return a list of available employees
     */
    @Override
    public List<Employee> findAvailableOperatorsExcludeDriver(final LocalDateTime start,
                                                              final LocalDateTime end,
                                                              final Employee driver) {
        List<Employee> available = tripDAO.findAvailableOperators(start, end);
        if (driver != null) {
            available.remove(driver);
        }
        return available;
    }

    /**
     * Retrieves qualified employees for editing a trip.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @param allowedLicences the licences to filter employees
     * @param tripToEdit the trip being edited
     * @return a list of qualified employees
     */
    @Override
    public List<Employee> findQualifiedDriversToEdit(final LocalDateTime start,
                                                     final LocalDateTime end,
                                                     final List<Licence> allowedLicences,
                                                     final Trip tripToEdit) {
        return tripDAO.findAvailableOperatorsForEdit(start, end, tripToEdit).stream()
                .filter(e -> allowedLicences.contains(e.getLicence()))
                .toList();
    }

    /**
     * Retrieves available employees for editing a trip, excluding a specific driver.
     *
     * @param start the start datetime
     * @param end the end datetime
     * @param driver the employee to exclude
     * @param tripToEdit the trip being edited
     * @return a list of available employees
     */
    @Override
    public List<Employee> findAvailableOperatorsExcludeDriverToEdit(
            final LocalDateTime start,
            final LocalDateTime end,
            final Employee driver,
            final Trip tripToEdit) {
        List<Employee> available =
                tripDAO.findAvailableOperatorsForEdit(start, end, tripToEdit);
        if (driver != null) {
            available.remove(driver);
        }
        return available;
    }

    /**
     * Retrieves postal codes with available trips on a specific date.
     *
     * @param date the date to check
     * @return a list of available postal codes
     */
    @Override
    public List<String> findAvailablePostalCodes(final LocalDate date) {
        return tripDAO.findAvailablePostalCodes(date);
    }

    /**
     * Retrieves the last 5 modified trips.
     *
     * @return a list of the 5 most recently modified trips
     */
    @Override
    public List<Trip> findLast5Modified() {
        return tripDAO.findLast5Modified();
    }

    /**
     * Counts the number of completed trips.
     *
     * @return the total number of completed trips
     */
    @Override
    public int countCompleted() {
        return tripDAO.countCompleted();
    }
}
