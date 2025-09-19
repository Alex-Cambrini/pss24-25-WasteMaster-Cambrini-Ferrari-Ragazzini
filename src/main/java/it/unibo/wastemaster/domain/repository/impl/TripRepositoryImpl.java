package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.infrastructure.dao.TripDAO;

import java.time.LocalDate;
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
    public List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end) {
        return tripDAO.findAvailableVehicles(start, end);
    }

    @Override
    public List<Employee> findQualifiedDrivers(LocalDateTime start, LocalDateTime end,
            List<Licence> allowedLicences) {
        List<Employee> available = tripDAO.findAvailableOperators(start, end);

        return available.stream()
                .filter(e -> allowedLicences.contains(e.getLicence()))
                .toList();
    }

    @Override
    public List<Employee> findAvailableOperatorsExcludeDriver(LocalDateTime start, LocalDateTime end, Employee driver) {
        List<Employee> available = tripDAO.findAvailableOperators(start, end);
        if (driver != null) {
            available.remove(driver);
        }
        return available;
    }

    @Override
    public List<String> findAvailablePostalCodes(LocalDate date) {
        return tripDAO.findAvailablePostalCodes(date);
    }
    
}

