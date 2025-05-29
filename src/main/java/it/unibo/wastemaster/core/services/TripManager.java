package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Vehicle;
import java.time.LocalDateTime;
import java.util.List;

public class TripManager {

    private final TripDAO tripDAO;

    public TripManager(TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    public void createTrip(String postalCode, Vehicle assignedVehicle,
                           List<Employee> operators,
                           LocalDateTime departureTime, LocalDateTime expectedReturnTime,
                           Trip.TripStatus status, List<Collection> collections) {
        Trip trip = new Trip(postalCode, assignedVehicle, operators, departureTime,
                expectedReturnTime, status, collections);
        tripDAO.insert(trip);
    }

    public void updateTrip(int tripId, String postalCode, Vehicle assignedVehicle,
                           List<Employee> operators,
                           LocalDateTime departureTime, LocalDateTime expectedReturnTime,
                           Trip.TripStatus status, List<Collection> collections) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            trip.setPostalCodes(postalCode);
            trip.setAssignedVehicle(assignedVehicle);
            trip.setOperators(operators);
            trip.setDepartureTime(departureTime);
            trip.setExpectedReturnTime(expectedReturnTime);
            trip.setStatus(status);
            trip.setCollections(collections);
            tripDAO.update(trip);
        }
    }

    public Trip getTripById(int tripId) {
        return tripDAO.findById(tripId);
    }

    public void deleteTrip(int tripId) {
        Trip trip = tripDAO.findById(tripId);
        if (trip != null) {
            tripDAO.delete(trip);
        }
    }

}



