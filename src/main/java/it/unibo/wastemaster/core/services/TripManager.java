package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Vehicle;


import java.time.LocalDateTime;
import java.util.List;
public class TripManager {

    private final TripDAO tripDAO;

    public TripManager(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void createTrip(String postalCode, Vehicle assignedVehicle, List<Employee> operators, 
    LocalDateTime departureTime, LocalDateTime expectedReturnTime, 
    Trip.TripStatus status) {
        Trip trip = new Trip( postalCode, assignedVehicle, operators, departureTime, 
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

    
    public List<Trip> getTripsByPostalCode(String postalCode) {
        return tripDAO.findByPostalCode(postalCode);
    }

    
    public List<Trip> getTripsByOperator(Employee operator) {
        return tripDAO.findByOperator(operator);
    }

    
    public List<Trip> getTripsByVehicle(Vehicle vehicle) {
        return tripDAO.findByVehicle(vehicle);
    }
   

    public List<Trip> getTripsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return tripDAO.findByDateRange(startDate, endDate);
    }

     
         public long countTripsByStatus(Trip.TripStatus status) {
         return tripDAO.countByStatus(status);
     }
}



