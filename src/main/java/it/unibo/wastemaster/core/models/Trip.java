package it.unibo.wastemaster.core.models;

import java.time.LocalDateTime;
import java.util.List;

public class Trip {
    private int tripId;                
    private Location postalCodes;  
    private Vehicle assignedVehicle;   
    private List<Employee> operators;  
    private LocalDateTime departureTime;  
    private LocalDateTime expectedReturnTime; 
    private TripStatus status;         
    private List<Collection> collections;
    
    public enum TripStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELED
    }

    
    public Trip(int tripId, Location postalCodes, Vehicle assignedVehicle, 
                List<Employee> operators, LocalDateTime departureTime, 
                LocalDateTime expectedReturnTime, TripStatus status,List<Collection> collections) {
        this.tripId = tripId;
        this.postalCodes = postalCodes;
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

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public Location getPostalCodes() {
        return postalCodes;
    }

    public void setPostalCodes(Location postalCodes) {
        this.postalCodes = postalCodes;
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

  

