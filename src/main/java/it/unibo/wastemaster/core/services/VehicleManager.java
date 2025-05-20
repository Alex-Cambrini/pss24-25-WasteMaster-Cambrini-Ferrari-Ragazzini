package it.unibo.wastemaster.core.services;

import java.time.LocalDate;

import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class VehicleManager {

	private final VehicleDAO vehicleDAO;

	public VehicleManager(VehicleDAO vehicleDAO) {
		this.vehicleDAO = vehicleDAO;
	}

	public Vehicle addVehicle(Vehicle vehicle) {
		ValidateUtils.validateEntity(vehicle);

		if (isPlateRegistered(vehicle.getPlate())) {
			throw new IllegalArgumentException(
					String.format("Cannot add vehicle: the plate '%s' is already registered.", vehicle.getPlate()));
		}
		vehicleDAO.insert(vehicle);
		return vehicle;
	}

	public Vehicle findVehicleByPlate(String plate) {
		if (plate == null) {
			throw new IllegalArgumentException("Plate cannot be null");
		}
		plate = plate.toUpperCase().trim();
		return vehicleDAO.findByPlate(plate);
	}

	private boolean isPlateRegistered(String plate) {
		Vehicle vehicle = findVehicleByPlate(plate);
		return vehicle != null;
	}

	public void updateVehicle(Vehicle vehicle) {
		ValidateUtils.validateEntity(vehicle);
		vehicleDAO.update(vehicle);
	}

	public void markMaintenanceAsComplete(Vehicle vehicle) {
		ValidateUtils.validateEntity(vehicle);
		if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_MAINTENANCE) {
			vehicle.setVehicleStatus(Vehicle.VehicleStatus.IN_SERVICE);
			vehicle.setLastMaintenanceDate(LocalDate.now());
			vehicle.setNextMaintenanceDate(vehicle.getLastMaintenanceDate().plusYears(1));
			updateVehicle(vehicle);
		} else {
			throw new IllegalArgumentException("The vehicle is not in maintenance status.");
		}
	}

	public boolean deleteVehicle(Vehicle vehicle) {
		try {
			ValidateUtils.requireArgNotNull(vehicle, "Vehicle cannot be null");
			ValidateUtils.requireArgNotNull(vehicle.getPlate(), "Vehicle plate cannot be null");
			vehicleDAO.delete(vehicle);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
