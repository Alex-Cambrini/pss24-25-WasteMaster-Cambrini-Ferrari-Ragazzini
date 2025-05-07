package it.unibo.wastemaster.core.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unibo.wastemaster.core.models.Trip;

public class TripManager {

    private final List<Trip> trips;

    public TripManager() {
        this.trips = new ArrayList<>();
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public boolean removeTripById(int tripId) {
        return trips.removeIf(trip -> trip.getTripId() == tripId);
    }

    public Optional<Trip> getTripById(int tripId) {
        return trips.stream()
                .filter(trip -> trip.getTripId() == tripId)
                .findFirst();
    }

    public List<Trip> getAllTrips() {
        return new ArrayList<>(trips);
    }

    public boolean updateTripStatus(int tripId, Trip.TripStatus newStatus) {
        Optional<Trip> optionalTrip = getTripById(tripId);
        if (optionalTrip.isPresent()) {
            optionalTrip.get().setStatus(newStatus);
            return true;
        }
        return false;
    }

    public List<Trip> getTripsByPostalCode(String postalCode) {
        return trips.stream()
                .filter(trip -> trip.getPostalCodes().contains(postalCode))
                .collect(Collectors.toList());
    }


}







