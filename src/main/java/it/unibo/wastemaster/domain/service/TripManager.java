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

    /**
     * Constructs a TripManager with the given repositories and managers.
     *
     * @param tripRepository the repository for trip data
     * @param collectionRepository the repository for collection data
     * @param recurringScheduleManager the manager for recurring schedules
     */
    public TripManager(final TripRepository tripRepository,
            final CollectionRepository collectionRepository,
            final RecurringScheduleManager recurringScheduleManager) {
        this.tripRepository = tripRepository;
        this.collectionRepository = collectionRepository;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    /**
     * Creates a new trip with the specified parameters and associates collections to it.
     *
     * @param postalCode the postal code for the trip
     * @param assignedVehicle the vehicle assigned to the trip
     * @param operators the list of operators assigned to the trip
     * @param departureTime the departure time of the trip
     * @param expectedReturnTime the expected return time of the trip
     * @param collections the collections to associate with the trip
     */
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

    /**
     * Retrieves the list of vehicles available between the specified start and end times.
     *
     * @param start the start time
     * @param end the end time
     * @return a list of available vehicles
     */
    public List<Vehicle> getAvailableVehicles(final LocalDateTime start, final LocalDateTime end) {
        return tripRepository.findAvailableVehicles(start, end);
    }

    /**
     * Retrieves the list of available operators, excluding the specified driver, for the given time range.
     *
     * @param start the start time
     * @param end the end time
     * @param driver the driver to exclude
     * @return a list of available operators excluding the driver
     */
    public List<Employee> getAvailableOperatorsExcludeDriver(LocalDateTime start, LocalDateTime end, Employee driver) {
        return tripRepository.findAvailableOperatorsExcludeDriver(start, end, driver);
    }

    /**
     * Retrieves the list of qualified drivers available in the given time range and with allowed licences.
     *
     * @param start the start time
     * @param end the end time
     * @param allowedLicences the list of allowed licences
     * @return a list of qualified drivers
     */
    public List<Employee> getQualifiedDrivers(LocalDateTime start, LocalDateTime end, List<Licence> allowedLicences) {
        return tripRepository.findQualifiedDrivers(start, end, allowedLicences);
    }

    /**
     * Retrieves a trip by its unique identifier.
     *
     * @param tripId the unique identifier of the trip
     * @return an Optional containing the trip if found, or empty if not found
     */
    public Optional<Trip> getTripById(final int tripId) {
        return tripRepository.findById(tripId);
    }

    /**
     * Sets the notification service used for sending trip-related notifications.
     *
     * @param notificationService the notification service to set
     */
    public void setNotificationService(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Soft deletes (cancels) the specified trip if it is ACTIVE.
     * Sets the trip status to CANCELED and updates related collections.
     *
     * @param trip the trip to cancel
     * @return true if the trip was successfully canceled, false otherwise
     */
    public boolean softDeleteTrip(final Trip trip) {
        try {
            ValidateUtils.requireArgNotNull(trip, "Trip cannot be null");
            ValidateUtils.requireArgNotNull(trip.getTripId(), "Trip ID cannot be null");

            if (trip.getStatus() != TripStatus.ACTIVE) {
                throw new IllegalArgumentException("Only ACTIVE trips can be canceled");
            }
            trip.setStatus(TripStatus.CANCELED);
            trip.setLastModified(LocalDateTime.now());
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

    /**
     * Soft deletes the trip and reschedules the next collection for each associated collection.
     *
     * @param trip the trip to cancel and reschedule
     * @return true if the trip was canceled and rescheduling succeeded, false otherwise
     */
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

    /**
     * Retrieves the list of available operators (excluding the selected driver) for editing a trip.
     *
     * @param depDateTime the new departure time
     * @param retDateTime the new return time
     * @param selectedDriver the driver to exclude
     * @param tripToEdit the trip being edited
     * @return a list of available operators for editing
     */
    public List<Employee> getAvailableOperatorsExcludeDriverToEdit(LocalDateTime depDateTime, LocalDateTime retDateTime,
            Employee selectedDriver,
            Trip tripToEdit) {
        return tripRepository.findAvailableOperatorsExcludeDriverToEdit(depDateTime,
                retDateTime, selectedDriver, tripToEdit);
    }

    /**
     * Retrieves the list of qualified drivers for editing a trip.
     *
     * @param depDateTime the new departure time
     * @param retDateTime the new return time
     * @param allowedLicences the list of allowed licences
     * @param tripToEdit the trip being edited
     * @return a list of qualified drivers for editing
     */
    public List<Employee> getQualifiedDriversToEdit(LocalDateTime depDateTime, LocalDateTime retDateTime,
            List<Licence> allowedLicences, Trip tripToEdit) {
        return tripRepository.findQualifiedDriversToEdit(depDateTime, retDateTime, allowedLicences, tripToEdit);
    }

    /**
     * Represents the possible results of a trip cancellation and notification attempt.
     */
    public enum CancellationResult {
        CANCELLED_AND_NOTIFIED,
        CANCELLED_NOTIFICATION_FAILED,
        CANCEL_FAILED
    }

    /**
     * Cancels the specified trip and notifies the associated customers with a default message.
     *
     * @param trip the trip to cancel and notify about
     * @return the result of the cancellation and notification attempt
     */
    public CancellationResult cancelTripAndNotify(final Trip trip) {
        final String subject = "Trip cancellation due to technical issues";
        final String body = "Dear customer,\n\n" +
                "We regret to inform you that your trip has been cancelled due to technical issues. " +
                "We apologize for the inconvenience and remain available to reschedule.\n\n" +
                "Best regards,\nWasteMaster";
        return cancelTripAndNotify(trip, subject, body);
    }

    /**
     * Cancels the specified trip and notifies the associated customers with a custom message.
     *
     * @param trip the trip to cancel and notify about
     * @param subject the subject of the notification
     * @param body the body of the notification
     * @return the result of the cancellation and notification attempt
     */
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

    /**
     * Marks the specified trip as COMPLETED if all associated collections are ACTIVE.
     * Sets all collections as COMPLETED and reschedules next collections if needed.
     *
     * @param trip the trip to mark as completed
     * @return true if the trip was successfully completed, false otherwise
     */
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
            trip.setLastModified(LocalDateTime.now());
            tripRepository.update(trip);

            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Updates the vehicle assigned to the specified trip.
     *
     * @param tripId the ID of the trip to update
     * @param newVehicle the new vehicle to assign
     * @throws IllegalArgumentException if the trip is not found or the vehicle is null
     */
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

    /**
     * Updates the operators assigned to the specified trip.
     *
     * @param tripId the ID of the trip to update
     * @param newOperators the new list of operators to assign
     * @throws IllegalArgumentException if the trip is not found or the operators list is invalid
     */
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

    /**
     * Retrieves the list of trips visible to the current user.
     * Administrators and office workers see all trips, others see only their own.
     *
     * @param currentUser the current user
     * @return a list of trips visible to the user
     */
    public List<Trip> getTripsForCurrentUser(Employee currentUser) {
        if (currentUser.getRole() == ADMINISTRATOR || currentUser.getRole() == OFFICE_WORKER) {
            return tripRepository.findAll();
        } else {
            return tripRepository.findByOperator(currentUser);
        }
    }

    /**
     * Retrieves the list of available postal codes for the specified date.
     *
     * @param date the date to check for available postal codes
     * @return a list of available postal codes
     */
    public List<String> getAvailablePostalCodes(LocalDate date) {
        return tripRepository.findAvailablePostalCodes(date);
    }

    /**
     * Retrieves the list of collections associated with the specified trip.
     *
     * @param trip the trip whose collections are to be retrieved
     * @return a list of collections for the trip
     */
    public List<Collection> getCollectionsByTrip(Trip trip) {
        return trip.getCollections();
    }

    /**
     * Updates the specified trip in the repository.
     *
     * @param trip the trip to update
     */
    public void updateTrip(Trip trip) {
        tripRepository.update(trip);
    }

    /**
     * Counts the number of completed trips in the system.
     *
     * @return the number of completed trips
     */
    public int countCompletedTrips() {
        return tripRepository.countCompleted();
    }

    /**
     * Represents the types of issues that can occur with a trip.
     */
    public enum IssueType {
        VEHICLE_PROBLEM,
        OPERATOR_PROBLEM,
        CANCELLATION
    }
}
