package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository {

    void save(Trip trip);

    void update(Trip trip);

    Optional<Trip> findById(int tripId);

    void delete(Trip trip);

    List<Trip> findAll();

    List<Collection> findCollectionsByPostalCode(String postalCode);

    List<Vehicle> findAvailableVehicles(LocalDateTime start, LocalDateTime end);

    List<Employee> findAvailableOperators(LocalDateTime start, LocalDateTime end);
}
