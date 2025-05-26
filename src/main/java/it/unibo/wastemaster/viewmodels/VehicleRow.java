package it.unibo.wastemaster.viewmodels;

import java.time.LocalDate;

import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Vehicle.RequiredLicence;
import it.unibo.wastemaster.core.models.Vehicle.VehicleStatus;

public class VehicleRow {
	private final String plate;
	private final String brand;
	private final String model;
	private final int registrationYear;
	private final int capacity;
	private final RequiredLicence licenceType;
	private final VehicleStatus vehicleStatus;
	private final LocalDate lastMaintenanceDate;
	private final LocalDate nextMaintenanceDate;

	public VehicleRow(Vehicle vehicle) {
		this.plate = vehicle.getPlate();
		this.brand = vehicle.getBrand();
		this.model = vehicle.getModel();
		this.registrationYear = vehicle.getRegistrationYear();
		this.capacity = vehicle.getCapacity();
		this.licenceType = vehicle.getRequiredLicence();
		this.vehicleStatus = vehicle.getVehicleStatus();
		this.lastMaintenanceDate = vehicle.getLastMaintenanceDate();
		this.nextMaintenanceDate = vehicle.getNextMaintenanceDate();
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

	public RequiredLicence getLicenceType() {
		return licenceType;
	}

	public VehicleStatus getVehicleStatus() {
		return vehicleStatus;
	}

	public LocalDate getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public LocalDate getNextMaintenanceDate() {
		return nextMaintenanceDate;
	}
}
