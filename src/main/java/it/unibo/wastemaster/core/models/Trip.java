package it.unibo.wastemaster.core.models;

import java.time.LocalDateTime;
import java.util.List;

public class Trip {
    private int tripId;                
    private List<String> postalCodes;  // Lista di CAP 
    private Vehicle assignedVehicle;   
    private List<Employee> operators;  
    private LocalDateTime departureTime;  
    private LocalDateTime expectedReturnTime; 
    private TripStatus status;         

    
    public enum TripStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELED
    }

    
    public Trip(int tripId, List<String> postalCodes, Vehicle assignedVehicle, 
                List<Employee> operators, LocalDateTime departureTime, 
                LocalDateTime expectedReturnTime, TripStatus status) {
        this.tripId = tripId;
        this.postalCodes = postalCodes;
        this.assignedVehicle = assignedVehicle;
        this.operators = operators;
        this.departureTime = departureTime;
        this.expectedReturnTime = expectedReturnTime;
        this.status = status;
    }

    
    
}

  

