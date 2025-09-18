package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Manages the creation, update, retrieval, and deletion of trips.
 */
public final class TripManager {

    private final TripRepository tripRepository;

    /**
     * Constructs a TripManager with a specified TripDAO.
     *
     * @param tripRepository the data access object for trips
     */
    public TripManager(final TripRepository tripRepository) {
        this.tripRepository = tripRepository;
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
            final List<Employee> operators, final LocalDateTime departureTime,
            final LocalDateTime expectedReturnTime, final Trip.TripStatus status,
            final List<Collection> collections) {

           
        if (assignedVehicle == null || assignedVehicle.getVehicleStatus() != Vehicle.VehicleStatus.IN_SERVICE) {
            throw new IllegalArgumentException("The assigned vehicle is not available or is not IN_SERVICE.");
        }

         
        
        List<Trip> overlappingTripsVehicle = tripRepository.findTripsByVehicleAndPeriod(
                assignedVehicle, departureTime, expectedReturnTime);
        if (!overlappingTripsVehicle.isEmpty()) {
            throw new IllegalArgumentException("The vehicle is already booked for other trips during the same period.");
        }

        
        for (Employee operator : operators) {
            List<Trip> overlappingTripsOperator = tripRepository.findTripsByOperatorAndPeriod(
                    operator, departureTime, expectedReturnTime);
            if (!overlappingTripsOperator.isEmpty()) {
                throw new IllegalArgumentException("The operator " + operator.getName() + " t is already booked for other trips during the same period.");
            }
        }

        
        List<Collection> collectionsForCap = getCollectionsByPostalCode(postalCode);
        if (collectionsForCap.isEmpty()) {
            throw new IllegalArgumentException("There are no collections for the specified postal code.");
        }

        Trip trip = new Trip(postalCode, assignedVehicle, operators, departureTime,
                expectedReturnTime, status, collections);
        tripRepository.save(trip);
    }

    /**
     * Updates an existing {@link Trip} in the repository.
     *
     * <p>
     * Validates the entity and ensures that the trip ID is not null before
     * performing the update.
     *
     * @param toUpdateTrip the trip to update; must not be null and must have a
     *                     valid ID
     * @throws IllegalArgumentException if {@code toUpdateTrip} is null or its ID is
     *                                  null
     */
     public void updateTrip(final Trip toUpdateTrip) {
        ValidateUtils.validateEntity(toUpdateTrip);
        ValidateUtils.requireArgNotNull(toUpdateTrip.getTripId(), "Trip ID cannot be null");

        
        List<Trip> overlappingTripsVehicle = tripRepository.findTripsByVehicleAndPeriod(
                toUpdateTrip.getAssignedVehicle(),
                toUpdateTrip.getDepartureTime(),
                toUpdateTrip.getExpectedReturnTime()
        ).stream()
         .filter(t -> t.getTripId() != toUpdateTrip.getTripId())
         .toList();
        if (!overlappingTripsVehicle.isEmpty()) {
            throw new IllegalArgumentException("The vehicle is already booked for other trips during the same period.");
        }

        
        for (Employee operator : toUpdateTrip.getOperators()) {
            List<Trip> overlappingTripsOperator = tripRepository.findTripsByOperatorAndPeriod(
                    operator,
                    toUpdateTrip.getDepartureTime(),
                    toUpdateTrip.getExpectedReturnTime()
            ).stream()
             .filter(t -> t.getTripId() != toUpdateTrip.getTripId())
             .toList();
            if (!overlappingTripsOperator.isEmpty()) {
                throw new IllegalArgumentException("The operator " + operator.getName() + " is already booked for other trips during the same period.");
            }
        }

        tripRepository.update(toUpdateTrip);
    }

    /**
     * Finds a trip by its unique identifier.
     *
     * @param tripId The ID of the trip to find.
     * @return An Optional containing the found Trip, or empty if not found.
     */
    public Optional<Trip> getTripById(final int tripId) {
        return tripRepository.findById(tripId);
    }

    /**
     * Deletes the specified trip from the database.
     *
     * @param trip The Trip object to delete; must not be null and must have a
     *             non-null tripId.
     * @return true if the trip was successfully deleted, false if the trip or its
     *         ID was null.
     */
    public boolean deleteTrip(final int tripId) {
        return tripRepository.findById(tripId)
                .map(trip -> {
                    tripRepository.delete(trip);
                    return true;
                })
                .orElse(false);
    }

    public void updateVehicle(int tripId, Vehicle newVehicle) {
        Optional<Trip> tripOpt = getTripById(tripId);
        if (tripOpt.isEmpty()) {
            throw new IllegalArgumentException("Trip not found");
        }

        ValidateUtils.requireArgNotNull(newVehicle, "new vehicle cannot be null");

        
        if (newVehicle.getVehicleStatus() != Vehicle.VehicleStatus.IN_SERVICE) {
            throw new IllegalArgumentException("The new vehicle is not available or is not IN_SERVICE.");
        }

        Trip trip = tripOpt.get();

        
        List<Trip> overlappingTripsVehicle = tripRepository.findTripsByVehicleAndPeriod(
                newVehicle,
                trip.getDepartureTime(),
                trip.getExpectedReturnTime()
        ).stream()
         .filter(t -> t.getTripId() != tripId)
         .toList();
        if (!overlappingTripsVehicle.isEmpty()) {
            throw new IllegalArgumentException("The vehicle is already booked for other trips during the same period.");
        }

        trip.setAssignedVehicle(newVehicle);
        tripRepository.update(trip);
    }

    public void updateOperators(int tripId, List<Employee> newOperators) {
        Optional<Trip> tripOpt = getTripById(tripId);
        if (tripOpt.isEmpty()) {
            throw new IllegalArgumentException("Trip not found");
        }

        ValidateUtils.requireArgNotNull(newOperators, "new operators cannot be null");
        ValidateUtils.requireListNotEmpty(newOperators, "The operators list cannot be null or empty");

        Trip trip = tripOpt.get();

        for (Employee operator : newOperators) {
           
            
            
            List<Trip> overlappingTripsOperator = tripRepository.findTripsByOperatorAndPeriod(
                    operator,
                    trip.getDepartureTime(),
                    trip.getExpectedReturnTime()
            ).stream()
             .filter(t -> t.getTripId() != tripId)
             .toList();
            if (!overlappingTripsOperator.isEmpty()) {
                throw new IllegalArgumentException("Operator " + operator.getName() + " is already booked for other trips during the same period.");
            }
        }

        trip.setOperators(newOperators);
        tripRepository.update(trip);
    }

    /**
     * Retrieves all trips from the database.
     *
     * @return A list of all trips.
     */
    public List<Trip> findAllTrips() {
        return tripRepository.findAll();
    }


    /**
     * Retrieves all collections for a given postal code (CAP).
     *
     * @param postalCode the postal code to filter collections
     * @return a list of collections associated with the given postal code
     */
    public List<Collection> getCollectionsByPostalCode(final String postalCode) {
        ValidateUtils.requireArgNotNull(postalCode, "Postal code cannot be null");
        return tripRepository.findCollectionsByPostalCode(postalCode);
    }


    public enum IssueType {
        VEHICLE_PROBLEM,
        OPERATOR_PROBLEM,
        CANCELLATION
    }
}
