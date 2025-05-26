package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleRow {
	private final String plate;
	private final String brand;
	private final String model;
	private final int registrationYear;
	private final int capacity;
	private final String licenceType;
	private final String vehicleStatus;
	private final String lastMaintenanceDate;
	private final String nextMaintenanceDate;

	public VehicleRow(Vehicle vehicle) {
		this.plate = vehicle.getPlate();
		this.brand = vehicle.getBrand();
		this.model = vehicle.getModel();
		this.registrationYear = vehicle.getRegistrationYear();
		this.capacity = vehicle.getCapacity();
		this.licenceType = vehicle.getRequiredLicence().toString();
		this.vehicleStatus = vehicle.getVehicleStatus().toString();
		this.lastMaintenanceDate = vehicle.getLastMaintenanceDate().toString();
		this.nextMaintenanceDate = vehicle.getNextMaintenanceDate().toString();
	}

	public String getPlate() {
		return plate;
	}

	public String getBrand() {
		return brand;
	}

	public String getModel() {
		return model;
	}

	public int getRegistrationYear() {
		return registrationYear;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getLicenceType() {
		return licenceType;
	}

	public String getVehicleStatus() {
		return vehicleStatus;
	}

	public String getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public String getNextMaintenanceDate() {
		return nextMaintenanceDate;
	}
}
