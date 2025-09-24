package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Trip.TripStatus;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Manages the creation, update, retrieval, and deletion of trips.
 */
public final class TripManager {

    private final TripRepository tripRepository;

    public TripManager(final TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createTrip(final String postalCode, final Vehicle assignedVehicle,
            final List<Employee> operators, final LocalDateTime departureTime,
            final LocalDateTime expectedReturnTime, final List<Collection> collections) {

        Trip trip = new Trip(postalCode, assignedVehicle, operators, departureTime,
                expectedReturnTime, collections);

        for (Collection collection : collections) {
            collection.setTrip(trip);
        }

        tripRepository.save(trip);
    }

    public List<Vehicle> getAvailableVehicles(final LocalDateTime start, final LocalDateTime end) {
        return tripRepository.findAvailableVehicles(start, end);
    }

    public List<Employee> getAvailableOperatorsExcludeDriver(LocalDateTime start, LocalDateTime end, Employee driver) {
        return tripRepository.findAvailableOperatorsExcludeDriver(start, end, driver);
    }

    public List<Employee> getQualifiedDrivers(LocalDateTime start, LocalDateTime end, List<Licence> allowedLicences) {
        return tripRepository.findQualifiedDrivers(start, end, allowedLicences);
    }

    public Optional<Trip> getTripById(final int tripId) {
        return tripRepository.findById(tripId);
    }

    public boolean softDeleteTrip(final Trip trip) {
        try {
            ValidateUtils.requireArgNotNull(trip, "Trip cannot be null");
            ValidateUtils.requireArgNotNull(trip.getTripId(),
                    "Trip ID cannot be null");
            trip.setStatus(TripStatus.CANCELED);
            tripRepository.update(trip);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void updateVehicle(int tripId, Vehicle newVehicle) {
        Optional<Trip> tripOpt = getTripById(tripId);
        if (tripOpt.isEmpty()) {
            throw new IllegalArgumentException("Trip not found");
        }

        ValidateUtils.requireArgNotNull(newVehicle, "new vehicle cannot be null");

        Trip trip = tripOpt.get();
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
        trip.setOperators(newOperators);
        tripRepository.update(trip);
    }

    public List<Trip> findAllTrips() {
        return tripRepository.findAll();
    }

    public List<String> getAvailablePostalCodes(LocalDate date) {
        return tripRepository.findAvailablePostalCodes(date);
    }

    public void updateTrip(Trip trip) {
    tripRepository.update(trip); 
    }

    public enum IssueType {
        VEHICLE_PROBLEM,
        OPERATOR_PROBLEM,
        CANCELLATION
    }
}
