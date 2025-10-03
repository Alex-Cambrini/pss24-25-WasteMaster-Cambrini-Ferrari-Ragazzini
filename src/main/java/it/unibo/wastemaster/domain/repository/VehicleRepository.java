package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Vehicle;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Vehicle entities.
 * Provides basic CRUD operations and retrieval by plate.
 */
public interface VehicleRepository {

    /**
     * Persists a new vehicle.
     *
     * @param vehicle the Vehicle entity to save
     */
    void save(Vehicle vehicle);

    /**
     * Retrieves a vehicle by its license plate.
     *
     * @param plate the license plate of the vehicle
     * @return an Optional containing the Vehicle if found, or empty if not found
     */
    Optional<Vehicle> findByPlate(String plate);

    /**
     * Updates an existing vehicle.
     *
     * @param vehicle the Vehicle entity to update
     */
    void update(Vehicle vehicle);

    /**
     * Deletes a vehicle.
     *
     * @param vehicle the Vehicle entity to delete
     */
    void delete(Vehicle vehicle);

    /**
     * Retrieves all vehicles.
     *
     * @return a list of all Vehicle entities
     */
    List<Vehicle> findAll();
}
