package it.unibo.wastemaster.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trip")
public class Trip {

    public enum TripStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripId;

    @Column(nullable = false)
    private String postalCode;  

    @ManyToOne
    private Vehicle assignedVehicle;

    @ManyToMany
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

    @OneToMany(mappedBy = "trip")
    private List<Collection> collections;
    
    public Trip(int tripId, String postalCode, Vehicle assignedVehicle, 
                List<Employee> operators, LocalDateTime departureTime, 
                LocalDateTime expectedReturnTime, TripStatus status,List<Collection> collections) {
        this.tripId = tripId;
        this.postalCode = postalCode;
        this.assignedVehicle = assignedVehicle;
        this.operators = operators;
        this.departureTime = departureTime;
        this.expectedReturnTime = expectedReturnTime;
        this.status = status;
        this.collections = collections;
    }

    public int getTripId() {
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
    
}

  

