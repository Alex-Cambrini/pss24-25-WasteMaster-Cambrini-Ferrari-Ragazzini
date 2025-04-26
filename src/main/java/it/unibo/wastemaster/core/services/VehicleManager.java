package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.models.Vehicle;
import jakarta.validation.ConstraintViolationException;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class VehicleManager {
	private final VehicleDAO vehicleDAO;

	public VehicleManager(VehicleDAO vehicleDAO) {
		this.vehicleDAO = vehicleDAO;
	}

	public Vehicle addVehicle(String plate, String brand, String model, int registrationYear,
			Vehicle.LicenceType licenceType, Vehicle.VehicleStatus status) {
		if (plate == null || plate.isBlank()) {
			throw new IllegalArgumentException("Plate cannot be null or blank");
		}
		if (vehicleDAO.findByPlate(plate) != null) {
			throw new IllegalArgumentException("Vehicle with this plate already exists");
		}

		Vehicle vehicle = new Vehicle(plate, brand, model, registrationYear, licenceType, status);
		ValidateUtils.VALIDATOR.validate(vehicle).stream().findFirst().ifPresent(v -> {
			throw new ConstraintViolationException("Validation failed", java.util.Set.of(v));
		});
		vehicleDAO.insert(vehicle);
		return vehicle;
	}

	public void updateStatus(String plate, Vehicle.VehicleStatus newStatus) {
		if (plate == null || plate.isBlank()) {
			throw new IllegalArgumentException("Plate cannot be null or blank");
		}
		if (newStatus == null) {
			throw new IllegalArgumentException("New status cannot be null");
		}

		Vehicle vehicle = vehicleDAO.findByPlate(plate);
		if (vehicle == null) {
			throw new IllegalArgumentException("Vehicle not found with plate: " + plate);
		}
		vehicle.setVehicleStatus(newStatus);
		vehicleDAO.update(vehicle);
	}
}
