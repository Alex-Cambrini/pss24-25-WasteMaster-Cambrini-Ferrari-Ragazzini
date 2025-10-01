package it.unibo.wastemaster.domain.service;

import static it.unibo.wastemaster.domain.model.Employee.Role.ADMINISTRATOR;
import static it.unibo.wastemaster.domain.model.Employee.Role.OFFICE_WORKER;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Trip.TripStatus;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages the creation, update, retrieval, and deletion of trips.
 */
public final class TripManager {

    private final TripRepository tripRepository;
    private final CollectionRepository collectionRepository;
    private final RecurringScheduleManager recurringScheduleManager;
    private NotificationService notificationService;

    public TripManager(final TripRepository tripRepository,
                       final CollectionRepository collectionRepository,
                       final RecurringScheduleManager recurringScheduleManager) {
        this.tripRepository = tripRepository;
        this.collectionRepository = collectionRepository;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    public void createTrip(final String postalCode, final Vehicle assignedVehicle,
            final List<Employee> operators, final LocalDateTime departureTime,
            final LocalDateTime expectedReturnTime, final List<Collection> collections) {

        Trip trip = new Trip(postalCode, assignedVehicle, operators, departureTime, expectedReturnTime,
                new ArrayList<>());
        tripRepository.save(trip);

        for (Collection collection : collections) {
            collection.setTrip(trip);
            collectionRepository.save(collection);
            trip.getCollections().add(collection);
        }
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

    public void setNotificationService(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public boolean softDeleteTrip(final Trip trip) {
        try {
            ValidateUtils.requireArgNotNull(trip, "Trip cannot be null");
            ValidateUtils.requireArgNotNull(trip.getTripId(), "Trip ID cannot be null");

            if (trip.getStatus() != TripStatus.ACTIVE) {
                throw new IllegalArgumentException("Only ACTIVE trips can be canceled");
            }
            trip.setStatus(TripStatus.CANCELED);
            tripRepository.update(trip);
            for (Collection c : trip.getCollections()) {
                c.setTrip(null);
                collectionRepository.update(c);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean softDeleteAndRescheduleNextCollection(final Trip trip) {
        boolean deleted = softDeleteTrip(trip);
        if (!deleted) {
            return false;
        }
        for (Collection collection : trip.getCollections()) {
            collection.setCollectionStatus(Collection.CollectionStatus.CANCELLED);
                collectionRepository.update(collection);
                recurringScheduleManager.rescheduleNextCollection(collection);
        }
        return true;
    }

    public List<Employee> getAvailableOperatorsExcludeDriverToEdit(LocalDateTime depDateTime, LocalDateTime retDateTime, Employee selectedDriver,
                                                                   Trip tripToEdit) {
        return tripRepository.findAvailableOperatorsExcludeDriverToEdit(depDateTime,
                retDateTime, selectedDriver, tripToEdit);
    }

    public List<Employee> getQualifiedDriversToEdit(LocalDateTime depDateTime, LocalDateTime retDateTime, List<Licence> allowedLicences, Trip tripToEdit) {
        return tripRepository.findQualifiedDriversToEdit(depDateTime, retDateTime, allowedLicences, tripToEdit);
    }

    public enum CancellationResult {
        CANCELLED_AND_NOTIFIED,
        CANCELLED_NOTIFICATION_FAILED,
        CANCEL_FAILED
    }

    public CancellationResult cancelTripAndNotify(final Trip trip) {
        final String subject = "Trip cancellation due to technical issues";
        final String body = "Dear customer,\n\n" +
                "We regret to inform you that your trip has been cancelled due to technical issues. " +
                "We apologize for the inconvenience and remain available to reschedule.\n\n" +
                "Best regards,\nWasteMaster";
        return cancelTripAndNotify(trip, subject, body);
    }

    public CancellationResult cancelTripAndNotify(final Trip trip,
            final String subject,
            final String body) {
        final boolean cancelled = softDeleteTrip(trip);
        if (!cancelled) {
            return CancellationResult.CANCEL_FAILED;
        }
        if (notificationService == null) {
            return CancellationResult.CANCELLED_NOTIFICATION_FAILED;
        }
        try {
            List<String> recipients = trip.getCollections().stream()
                    .map(Collection::getCustomer)
                    .filter(Objects::nonNull)
                    .map(Customer::getEmail)
                    .distinct()
                    .toList();

            notificationService.notifyTripCancellation(recipients, subject, body);
            return CancellationResult.CANCELLED_AND_NOTIFIED;
        } catch (Exception ex) {
            return CancellationResult.CANCELLED_NOTIFICATION_FAILED;
        }
    }

    public boolean setTripAsCompleted(final Trip trip) {
        try {
            ValidateUtils.requireArgNotNull(trip, "Trip cannot be null");
            ValidateUtils.requireArgNotNull(trip.getTripId(), "Trip ID cannot be null");

            if (trip.getStatus() != TripStatus.ACTIVE) {
                throw new IllegalArgumentException("Only ACTIVE trips can be set as COMPLETED");
            }

            for (Collection c : trip.getCollections()) {
                if (c.getCollectionStatus() != Collection.CollectionStatus.ACTIVE) {
                    throw new IllegalArgumentException("All collections must be ACTIVE to complete the trip");
                }
                c.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
                collectionRepository.update(c);
                recurringScheduleManager.rescheduleNextCollection(c);
            }

            trip.setStatus(TripStatus.COMPLETED);
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

    public List<Trip> getTripsForCurrentUser(Employee currentUser) {
        if (currentUser.getRole() == ADMINISTRATOR || currentUser.getRole() == OFFICE_WORKER) {
            return tripRepository.findAll();
        } else {
            return tripRepository.findByOperator(currentUser);
        }
    }

    public List<String> getAvailablePostalCodes(LocalDate date) {
        return tripRepository.findAvailablePostalCodes(date);
    }

    public List<Collection> getCollectionsByTrip(Trip trip) {
        return trip.getCollections();
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
