package it.unibo.wastemaster.core.models;

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

@Entity
@Table(name = "trip")
public class Trip {

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
    @Column(nullable = false)
    @OneToMany(mappedBy = "trip")
    private List<Collection> collections;

    public Trip() {

    }

    public Trip(String postalCode, Vehicle assignedVehicle,
                List<Employee> operators, LocalDateTime departureTime,
                LocalDateTime expectedReturnTime, TripStatus status,
                List<Collection> collections) {

        this.postalCode = postalCode;
        this.assignedVehicle = assignedVehicle;
        this.operators = operators;
        this.departureTime = departureTime;
        this.expectedReturnTime = expectedReturnTime;
        this.status = status;
        this.collections = collections;
    }

    public Integer getTripId() {
        return tripId;
    }

    public String getPostalCodes() {
        return postalCode;
    }

    public void setPostalCodes(String postalCodes) {
        this.postalCode = postalCodes;
    }

    public Vehicle getAssignedVehicle() {
        return assignedVehicle;
    }

    public void setAssignedVehicle(Vehicle assignedVehicle) {
        this.assignedVehicle = assignedVehicle;
    }

    public List<Employee> getOperators() {
        return operators;
    }

    public void setOperators(List<Employee> operators) {
        this.operators = operators;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(LocalDateTime expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public String toString() {
        return String.format(
                "Trip {ID: %d, PostalCode: %s, Vehicle: %s, Operators: %s, Departure: "
                        + "%s, ExpectedReturn: %s, Status: %s, CollectionIDs: %s}",
                tripId,
                postalCode != null ? postalCode : "N/A",
                assignedVehicle != null ? assignedVehicle.getPlate() : "N/A",
                operators != null ? operators.stream()
                        .map(e -> e.getName() + " " + e.getSurname())
                        .reduce((a, b) -> a + ", " + b).orElse("None") : "N/A",
                departureTime != null ? departureTime.toString() : "N/A",
                expectedReturnTime != null ? expectedReturnTime.toString() : "N/A",
                status != null ? status.name() : "N/A",
                collections != null ? collections.stream()
                        .map(c -> String.valueOf(c.getCollectionId()))
                        .reduce((a, b) -> a + ", " + b).orElse("None") : "N/A");
    }

    public enum TripStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELED
    }
}
