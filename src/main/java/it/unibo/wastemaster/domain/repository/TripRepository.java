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

    
    /**
     * Finds all collections by postal code (CAP).
     *
     * @param postalCode the postal code to filter collections
     * @return a list of collections associated with the given postal code
     */
    List<Collection> findCollectionsByPostalCode(String postalCode);

    /**
     * Finds trips for a vehicle that overlap with a given period.
     */
    List<Trip> findTripsByVehicleAndPeriod(Vehicle vehicle, LocalDateTime start, LocalDateTime end);

    /**
     * Finds trips for an operator that overlap with a given period.
     */
    List<Trip> findTripsByOperatorAndPeriod(Employee operator, LocalDateTime start, LocalDateTime end);


}
