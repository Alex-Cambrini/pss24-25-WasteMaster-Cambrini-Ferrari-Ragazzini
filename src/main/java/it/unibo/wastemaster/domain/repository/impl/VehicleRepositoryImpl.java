package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.VehicleRepository;
import it.unibo.wastemaster.infrastructure.dao.VehicleDAO;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link VehicleRepository} that uses {@link VehicleDAO}
 * to perform CRUD operations on Vehicle entities.
 */
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleDAO vehicleDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param vehicleDAO the DAO used to access vehicle data
     */
    public VehicleRepositoryImpl(final VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    /**
     * Persists a new vehicle.
     *
     * @param vehicle the vehicle to save
     */
    @Override
    public void save(final Vehicle vehicle) {
        vehicleDAO.insert(vehicle);
    }

    /**
     * Retrieves a vehicle by its plate.
     *
     * @param plate the vehicle plate
     * @return an Optional containing the vehicle if found, or empty
     */
    @Override
    public Optional<Vehicle> findByPlate(final String plate) {
        return vehicleDAO.findByPlate(plate);
    }

    /**
     * Updates an existing vehicle.
     *
     * @param vehicle the vehicle to update
     */
    @Override
    public void update(final Vehicle vehicle) {
        vehicleDAO.update(vehicle);
    }

    /**
     * Deletes a vehicle.
     *
     * @param vehicle the vehicle to delete
     */
    @Override
    public void delete(final Vehicle vehicle) {
        vehicleDAO.delete(vehicle);
    }

    /**
     * Retrieves all vehicles.
     *
     * @return a list of all vehicles
     */
    @Override
    public List<Vehicle> findAll() {
        return vehicleDAO.findAll();
    }
}
