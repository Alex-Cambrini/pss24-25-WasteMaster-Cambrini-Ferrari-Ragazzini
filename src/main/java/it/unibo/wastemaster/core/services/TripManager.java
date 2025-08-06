package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Vehicle;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Manages the creation, update, retrieval, and deletion of trips.
 */
public final class TripManager {

    private final TripDAO tripDAO;

    /**
     * Constructs a TripManager with a specified TripDAO.
     *
     * @param tripDAO the data access object for trips
     */
    public TripManager(final TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    /**
     * Creates a new trip and persists it to the database.
     *
     * @param postalCode         The postal code of the trip's destination.
     * @param assignedVehicle    The vehicle assigned to the trip.
     * @param operators          The list of employees assigned as operators.
     * @param departureTime      The scheduled departure time.
     * @param expectedReturnTime The expected return time.
     * @param status             The initial status of the trip.
     * @param collections        The list of collections for the trip.
     */
    public void createTrip(final String postalCode, final Vehicle assignedVehicle,
                           final List<Employee> operators,
                           final LocalDateTime departureTime,
                           final LocalDateTime expectedReturnTime,
                           final Trip.TripStatus status,
                           final List<Collection> collections) {
        Trip trip = new Trip(postalCode, assignedVehicle, operators,
            departureTime, expectedReturnTime, status, collections);
        tripDAO.insert(trip);
    }

    /**
     * Updates an existing trip with new details.
     *
     * @param tripId             The ID of the trip to update.
     * @param postalCode         The new postal code.
     * @param assignedVehicle    The new assigned vehicle.
     * @param operators          The new list of operators.
     * @param departureTime      The new departure time.
     * @param expectedReturnTime The new expected return time.
     * @param status             The new status of the trip.
     * @param collections        The new list of collections.
     */
    public void updateTrip(final int tripId, final String postalCode,
                           final Vehicle assignedVehicle,
                           final List<Employee> operators,
                           final LocalDateTime departureTime,
                           final LocalDateTime expectedReturnTime,
                           final Trip.TripStatus status,
                           final List<Collection> collections) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            trip.setPostalCodes(postalCode);
            trip.setAssignedVehicle(assignedVehicle);
            trip.setOperators(operators);
            trip.setDepartureTime(departureTime);
            trip.setExpectedReturnTime(expectedReturnTime);
            trip.setStatus(status);
            trip.setCollections(collections);
            tripDAO.update(trip);
        }
    }

    /**
     * Finds a trip by its unique identifier.
     *
     * @param tripId The ID of the trip to find.
     * @return The found Trip object, or null if not found.
     */
    public Trip getTripById(final int tripId) {
        return tripDAO.findById(tripId);
    }

    /**
     * Deletes a trip from the database.
     *
     * @param tripId The ID of the trip to delete.
     */
    public void deleteTrip(final int tripId) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            tripDAO.delete(trip);
        }
    }
}
