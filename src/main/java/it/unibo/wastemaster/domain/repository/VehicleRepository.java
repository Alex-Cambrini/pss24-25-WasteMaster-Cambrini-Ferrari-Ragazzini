package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    void save(Vehicle vehicle);

    Optional<Vehicle> findByPlate(String plate);

    void update(Vehicle vehicle);

    void delete(Vehicle vehicle);

    List<Vehicle> findAll();
}
