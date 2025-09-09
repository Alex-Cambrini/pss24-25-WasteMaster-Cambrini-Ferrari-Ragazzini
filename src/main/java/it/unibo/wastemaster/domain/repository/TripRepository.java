package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Trip;
import java.util.List;
import java.util.Optional;

public interface TripRepository {

    void save(Trip trip);

    void update(Trip trip);

    Optional<Trip> findById(int tripId);

    void delete(Trip trip);

    List<Trip> findAll();
}
