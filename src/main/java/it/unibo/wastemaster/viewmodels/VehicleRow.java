package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Vehicle.RequiredLicence;
import it.unibo.wastemaster.domain.model.Vehicle.VehicleStatus;
import java.time.LocalDate;

/**
 * ViewModel class for representing a vehicle in table format.
 */
public final class VehicleRow {

    private final String plate;
    private final String brand;
    private final String model;
    private final int registrationYear;
    private final int capacity;
    private final RequiredLicence licenceType;
    private final VehicleStatus vehicleStatus;
    private final LocalDate lastMaintenanceDate;
    private final LocalDate nextMaintenanceDate;

    /**
     * Constructs a VehicleRow from a Vehicle entity.
     *
     * @param vehicle the vehicle to represent
     */
    public VehicleRow(final Vehicle vehicle) {
        this.plate = vehicle.getPlate();
        this.brand = vehicle.getBrand();
        this.model = vehicle.getModel();
        this.registrationYear = vehicle.getRegistrationYear();
        this.capacity = vehicle.getRequiredOperators();
        this.licenceType = vehicle.getRequiredLicence();
        this.vehicleStatus = vehicle.getVehicleStatus();
        this.lastMaintenanceDate = vehicle.getLastMaintenanceDate();
        this.nextMaintenanceDate = vehicle.getNextMaintenanceDate();
    }

    /**
     * Gets the vehicle plate.
     *
     * @return the plate
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Gets the brand of the vehicle.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Gets the model of the vehicle.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Gets the vehicle's registration year.
     *
     * @return the registration year
     */
    public int getRegistrationYear() {
        return registrationYear;
    }

    /**
     * Gets the capacity of the vehicle.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the required driving licence for the vehicle.
     *
     * @return the licence type
     */
    public RequiredLicence getLicenceType() {
        return licenceType;
    }

    /**
     * Gets the current status of the vehicle.
     *
     * @return the vehicle status
     */
    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    /**
     * Gets the date of last maintenance.
     *
     * @return the last maintenance date
     */
    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    /**
     * Gets the date of next scheduled maintenance.
     *
     * @return the next maintenance date
     */
    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }
}
