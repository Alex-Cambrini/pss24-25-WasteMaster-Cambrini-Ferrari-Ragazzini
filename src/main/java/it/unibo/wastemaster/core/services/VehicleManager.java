package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleManager {
	private final VehicleDAO vehicleDAO;

	public VehicleManager(VehicleDAO vehicleDAO) {
		this.vehicleDAO = vehicleDAO;
	}

	public Vehicle addVehicle(String plate, String brand, String model, int registrationYear,
			Vehicle.LicenceType licenceType, Vehicle.VehicleStatus status) {
		Vehicle vehicle = new Vehicle(plate, brand, model, registrationYear, licenceType, status);
		vehicleDAO.insert(vehicle);
		return vehicle;
	}

	public void updateStatus(String plate, Vehicle.VehicleStatus newStatus) {
		Vehicle vehicle = vehicleDAO.findByPlate(plate);
		if (vehicle != null) {
			vehicle.setVehicleStatus(newStatus);
			vehicleDAO.update(vehicle);
		}
	}
}
