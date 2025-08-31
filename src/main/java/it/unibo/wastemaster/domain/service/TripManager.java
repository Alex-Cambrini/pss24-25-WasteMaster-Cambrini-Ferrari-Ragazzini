package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Collection;
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
     * @param postalCode The postal code of the trip's destination.
     * @param assignedVehicle The vehicle assigned to the trip.
     * @param operators The list of employees assigned as operators.
     * @param departureTime The scheduled departure time.
     * @param expectedReturnTime The expected return time.
     * @param status The initial status of the trip.
     * @param collections The list of collections for the trip.
     */
    public void createTrip(final String postalCode, final Vehicle assignedVehicle,
            final List<Employee> operators, final LocalDateTime departureTime,
            final LocalDateTime expectedReturnTime, final Trip.TripStatus status,
            final List<Collection> collections) {
        Trip trip = new Trip(postalCode, assignedVehicle, operators, departureTime,
                expectedReturnTime, status, collections);
        tripRepository.save(trip);
    }

    /**
     * Aggiorna un viaggio esistente se trovato.
     *
     * @param tripId l'ID del viaggio da aggiornare
     * @param postalCode il nuovo codice postale
     * @param assignedVehicle il nuovo veicolo assegnato
     * @param operators la nuova lista di operatori
     * @param departureTime il nuovo orario di partenza
     * @param expectedReturnTime il nuovo orario di ritorno previsto
     * @param status il nuovo stato del viaggio
     * @param collections la nuova lista di raccolte
     */
    public void updateTrip(final int tripId, final String postalCode,
            final Vehicle assignedVehicle, final List<Employee> operators,
            final LocalDateTime departureTime, final LocalDateTime expectedReturnTime,
            final Trip.TripStatus status, final List<Collection> collections) {
        tripRepository.findById(tripId).ifPresent(trip -> {
            trip.setPostalCodes(postalCode);
            trip.setAssignedVehicle(assignedVehicle);
            trip.setOperators(operators);
            trip.setDepartureTime(departureTime);
            trip.setExpectedReturnTime(expectedReturnTime);
            trip.setStatus(status);
            trip.setCollections(collections);
            tripRepository.update(trip);
        });
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
     * @param trip The Trip object to delete; must not be null and must have a non-null tripId.
     * @return true if the trip was successfully deleted, false if the trip or its ID was null.
     */
    public boolean deleteTrip(final Trip trip) {
        try {
            ValidateUtils.requireArgNotNull(trip, "Trip cannot be null");
            ValidateUtils.requireArgNotNull(trip.getTripId(), "Trip ID cannot be null");
            tripRepository.delete(trip);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Handles modifications to a trip in case of unexpected events. Only non-null
     * parameters will be updated.
     *
     * @param tripId The ID of the trip to modify.
     * @param newVehicle The new vehicle to assign (null if unchanged).
     * @param newOperators The new list of operators (null if unchanged).
     * @param newDepartureTime The new departure time (null if unchanged).
     * @param newExpectedReturnTime The new expected return time (null if unchanged).
     * @param newCollections The new list of collections (null if unchanged).
     * @param newStatus The new status of the trip (null if unchanged).
     */
    public void handleUnexpectedEvent(final int tripId, final Vehicle newVehicle,
            final List<Employee> newOperators, final LocalDateTime newDepartureTime,
            final LocalDateTime newExpectedReturnTime,
            final List<Collection> newCollections, final Trip.TripStatus newStatus) {
        tripRepository.findById(tripId).ifPresent(trip -> {
            if (newVehicle != null) {
                trip.setAssignedVehicle(newVehicle);
            }
            if (newOperators != null) {
                trip.setOperators(newOperators);
            }
            if (newDepartureTime != null) {
                trip.setDepartureTime(newDepartureTime);
            }
            if (newExpectedReturnTime != null) {
                trip.setExpectedReturnTime(newExpectedReturnTime);
            }
            if (newCollections != null) {
                trip.setCollections(newCollections);
            }
            if (newStatus != null) {
                trip.setStatus(newStatus);
                // If the trip is cancelled, cancel all related collections
                if (newStatus == Trip.TripStatus.CANCELED) {
                    for (Collection collection : trip.getCollections()) {
                        collection.setCollectionStatus(
                                Collection.CollectionStatus.CANCELLED);

                    }

                }
            }
            tripRepository.update(trip);
        });
    }
    /**
     * Retrieves all trips from the database.
     *
     * @return A list of all trips.
     */
    public List<Trip> findAllTrip() {
    return tripRepository.findAll();
    }
}
