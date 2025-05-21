package it.unibo.wastemaster.viewmodels;

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

	public VehicleRow(String plate, String brand, String model, int registrationYear,
			String licenceType, String vehicleStatus,
			String lastMaintenanceDate, String nextMaintenanceDate,
			int capacity) {
		this.plate = plate;
		this.brand = brand;
		this.model = model;
		this.registrationYear = registrationYear;
		this.licenceType = licenceType;
		this.vehicleStatus = vehicleStatus;
		this.lastMaintenanceDate = lastMaintenanceDate;
		this.nextMaintenanceDate = nextMaintenanceDate;
		this.capacity = capacity;
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
