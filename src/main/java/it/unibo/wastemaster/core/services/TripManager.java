package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Vehicle;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
public class TripManager {

    private final TripDAO tripDAO;

    public TripManager(EntityManager entityManager) {
        this.tripDAO = new TripDAO(entityManager);
    }

    public void createTrip(String postalCode, Vehicle assignedVehicle, List<Employee> operators, 
    LocalDateTime departureTime, LocalDateTime expectedReturnTime, 
    Trip.TripStatus status) {
        Trip trip = new Trip(0, postalCode, assignedVehicle, operators, departureTime, 
        expectedReturnTime, status, null);
        tripDAO.insert(trip);
    }

    public void updateTrip(int tripId, String postalCode, Vehicle assignedVehicle, List<Employee> operators, 
    LocalDateTime departureTime, LocalDateTime expectedReturnTime, 
    Trip.TripStatus status) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            trip.setPostalCodes(postalCode);
            trip.setAssignedVehicle(assignedVehicle);
            trip.setOperators(operators);
            trip.setDepartureTime(departureTime);
            trip.setExpectedReturnTime(expectedReturnTime);
            trip.setStatus(status);
            tripDAO.update(trip);
        }
    }

     
     public Trip getTripById(int tripId) {
        return tripDAO.findById(tripId);
    }

    
    public List<Trip> getTripsByStatus(Trip.TripStatus status) {
        return tripDAO.findByStatus(status);
    }


    
    public void deleteTrip(int tripId) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            tripDAO.delete(trip);
        }
    }

    


}



