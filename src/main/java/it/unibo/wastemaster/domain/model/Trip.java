package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a trip assigned to a vehicle and
 * a list of employees to perform waste collections.
 */
@Entity
@Table(name = "trip")
public final class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripId;

    @Column(nullable = false)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle assignedVehicle;

    @ManyToMany
    @JoinTable(
            name = "trip_operators",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> operators;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expectedReturnTime;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TripStatus status;

    @NotNull(message = "collection cannot be null")
    @OneToMany(mappedBy = "trip")
    private List<Collection> collections;

    /**
     * Default constructor for JPA.
     */
    public Trip() {
    }

    /**
     * Constructs a new Trip with the specified details.
     *
     * @param postalCode the postal code associated with the trip
     * @param assignedVehicle the vehicle assigned to the trip
     * @param operators the list of employees (operators) for the trip
     * @param departureTime the scheduled departure time
     * @param expectedReturnTime the expected return time
     * @param collections the list of collections to be performed during the trip
     */
    public Trip(final String postalCode, final Vehicle assignedVehicle,
                final List<Employee> operators, final LocalDateTime departureTime,
                final LocalDateTime expectedReturnTime,
                final List<Collection> collections) {
        this.postalCode = postalCode;
        this.assignedVehicle = assignedVehicle;
        this.operators = operators;
        this.departureTime = departureTime;
        this.expectedReturnTime = expectedReturnTime;
        this.collections = collections;
    }

    /**
     * Gets the unique identifier of the trip.
     *
     * @return the trip ID
     */
    public Integer getTripId() {
        return tripId;
    }

    /**
     * Gets the postal code associated with the trip.
     *
     * @return the postal code
     */
    public String getPostalCodes() {
        return postalCode;
    }

    /**
     * Sets the postal code for the trip.
     *
     * @param postalCodes the new postal code
     */
    public void setPostalCodes(final String postalCodes) {
        this.postalCode = postalCodes;
    }

    /**
     * Gets the vehicle assigned to the trip.
     *
     * @return the assigned vehicle
     */
    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    /**
     * Sets the vehicle assigned to the trip.
     *
     * @param assignedVehicle the new assigned vehicle
     */
    public void setAssignedVehicle(final Vehicle assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    /**
     * Gets the list of employees (operators) for the trip.
     *
     * @return the list of operators
     */
    public List<Employee> getOperators() {
        return operators;
    }

    /**
     * Sets the list of operators for the trip.
     *
     * @param operators the new list of operators
     */
    public void setOperators(final List<Employee> operators) {
        this.operators = operators;
    }

    /**
     * Gets the scheduled departure time.
     *
     * @return the departure time
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the scheduled departure time.
     *
     * @param departureTime the new departure time
     */
    public void setDepartureTime(final LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Gets the expected return time.
     *
     * @return the expected return time
     */
    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    /**
     * Sets the expected return time.
     *
     * @param expectedReturnTime the new expected return time
     */
    public void setExpectedReturnTime(final LocalDateTime expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    /**
     * Gets the current status of the trip.
     *
     * @return the trip status
     */
    public TripStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the trip.
     *
     * @param status the new status
     */
    public void setStatus(final TripStatus status) {
        this.status = status;
    }

    /**
     * Gets the list of collections to be performed during the trip.
     *
     * @return the list of collections
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Sets the list of collections for the trip.
     *
     * @param collections the new list of collections
     */
    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * Enum for the status of a trip.
     */
    public enum TripStatus {
        ACTIVE, COMPLETED, CANCELED
    }
}
