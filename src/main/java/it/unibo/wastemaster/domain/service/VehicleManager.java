package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.VehicleRepository;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing vehicle entities.
 */
public final class VehicleManager {

    private final VehicleRepository vehicleRepository;

    /**
     * Constructs a new VehicleManager.
     *
     * @param vehicleRepository DAO for vehicle persistence
     */
    public VehicleManager(final VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Adds a new vehicle after validation.
     *
     * @param vehicle the vehicle to add
     * @return the persisted vehicle
     * @throws IllegalArgumentException if plate is already registered
     */
    public Vehicle addVehicle(final Vehicle vehicle) {
        ValidateUtils.validateEntity(vehicle);

        if (isPlateRegistered(vehicle.getPlate())) {
            throw new IllegalArgumentException(String.format(
                    "Cannot add vehicle: the plate '%s' is already registered.",
                    vehicle.getPlate()));
        }

        vehicleRepository.save(vehicle);
        return vehicle;
    }

    /**
     * Finds a vehicle by its plate.
     *
     * @param plate the plate to search for
     * @return an Optional containing the vehicle if found, or empty if not found
     * @throws IllegalArgumentException if plate is null
     */
    public Optional<Vehicle> findVehicleByPlate(final String plate) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate cannot be null");
        }
        final String normalizedPlate = plate.toUpperCase().trim();
        return vehicleRepository.findByPlate(normalizedPlate);
    }

    private boolean isPlateRegistered(final String plate) {
        return findVehicleByPlate(plate).isPresent();
    }

    /**
     * Updates an existing vehicle after validation.
     *
     * @param vehicle the vehicle to update
     * @throws IllegalArgumentException if the new plate is already used by another
     * vehicle
     */
    public void updateVehicle(final Vehicle vehicle) {
        ValidateUtils.validateEntity(vehicle);

        final String normalizedPlate = vehicle.getPlate().toUpperCase().trim();
        final Optional<Vehicle> existing = findVehicleByPlate(normalizedPlate);

        if (existing.isPresent()
                && existing.get().getVehicleId() != vehicle.getVehicleId()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot update vehicle: the plate '%s' is already registered to "
                            + "another vehicle.",
                    normalizedPlate));
        }

        vehicle.setPlate(normalizedPlate);
        vehicleRepository.update(vehicle);
    }

    /**
     * Marks maintenance as complete and updates dates.
     *
     * @param vehicle the vehicle being maintained
     * @throws IllegalArgumentException if vehicle is not in maintenance
     */
    public void markMaintenanceAsComplete(final Vehicle vehicle) {
        ValidateUtils.validateEntity(vehicle);

        if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_MAINTENANCE) {
            vehicle.setVehicleStatus(Vehicle.VehicleStatus.IN_SERVICE);
            vehicle.setLastMaintenanceDate(LocalDate.now());
            vehicle.setNextMaintenanceDate(vehicle.getLastMaintenanceDate().plusYears(1));
            updateVehicle(vehicle);
        } else {
            throw new IllegalArgumentException(
                    "The vehicle is not in maintenance status.");
        }
    }

    /**
     * Deletes the given vehicle if valid.
     *
     * @param vehicle the vehicle to delete
     * @return true if deleted, false if validation fails
     */
    public boolean deleteVehicle(final Vehicle vehicle) {
        try {
            ValidateUtils.requireArgNotNull(vehicle, "Vehicle cannot be null");
            ValidateUtils.requireArgNotNull(vehicle.getPlate(),
                    "Vehicle plate cannot be null");
            vehicleRepository.delete(vehicle);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<Vehicle> findAllVehicle() {
        return vehicleRepository.findAll();
    }
}
