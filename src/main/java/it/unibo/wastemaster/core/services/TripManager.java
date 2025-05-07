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

    public List<Trip> getTripsByStatus(Trip.TripStatus status) {
        return trips.stream()
                .filter(trip -> trip.getStatus() == status)
                .collect(Collectors.toList());
    }



}







