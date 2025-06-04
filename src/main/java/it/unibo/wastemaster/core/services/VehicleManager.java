package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import java.time.LocalDate;

/**
 * Service for managing vehicle entities.
 */
public final class VehicleManager {

    private final VehicleDAO vehicleDAO;

    /**
     * Constructs a new VehicleManager.
     *
     * @param vehicleDAO DAO for vehicle persistence
     */
    public VehicleManager(final VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
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

        vehicleDAO.insert(vehicle);
        return vehicle;
    }

    /**
     * Finds a vehicle by its plate.
     *
     * @param plate the plate to search for
     * @return the vehicle or null if not found
     */
    public Vehicle findVehicleByPlate(final String plate) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate cannot be null");
        }
        final String normalizedPlate = plate.toUpperCase().trim();
        return vehicleDAO.findByPlate(normalizedPlate);
    }

    private boolean isPlateRegistered(final String plate) {
        final Vehicle vehicle = findVehicleByPlate(plate);
        return vehicle != null;
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
        final Vehicle existing = findVehicleByPlate(normalizedPlate);

        if (existing != null && existing.getVehicleId() != vehicle.getVehicleId()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot update vehicle: the plate '%s' is already registered to "
                            + "another vehicle.",
                    normalizedPlate));
        }

        vehicle.setPlate(normalizedPlate);
        vehicleDAO.update(vehicle);
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
            vehicleDAO.delete(vehicle);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
