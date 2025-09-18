package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.dao.TripDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TripRepositoryImpl implements TripRepository {

    private final TripDAO tripDAO;

    public TripRepositoryImpl(final TripDAO tripDAO) {
        this.tripDAO = tripDAO;
    }

    @Override
    public void save(Trip trip) {
        tripDAO.insert(trip);
    }

    @Override
    public void update(Trip trip) {
        tripDAO.update(trip);
    }

    @Override
    public Optional<Trip> findById(int tripId) {
        return tripDAO.findById(tripId);
    }

    @Override
    public void delete(Trip trip) {
        tripDAO.delete(trip);
    }

    @Override
    public List<Trip> findAll() {
        return tripDAO.findAll();
    }

    @Override
    public List<Collection> findCollectionsByPostalCode(String postalCode) {
        return tripDAO.findCollectionsByPostalCode(postalCode);
    }

    @Override
    public List<Trip> findTripsByVehicleAndPeriod(Vehicle vehicle, LocalDateTime start, LocalDateTime end) {
        return tripDAO.findTripsByVehicleAndPeriod(vehicle, start, end);
    }

    @Override
    public List<Trip> findTripsByOperatorAndPeriod(Employee operator, LocalDateTime start, LocalDateTime end) {
        return tripDAO.findTripsByOperatorAndPeriod(operator, start, end);
    }
}
