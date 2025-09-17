package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * Represents a vehicle used for waste collection.
 */
@Entity
public final class Vehicle {

    /**
     * Unique identifier of the vehicle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vehicleId;

    /**
     * License plate of the vehicle.
     */
    @Column(length = 10, nullable = false, unique = true)
    @NotBlank(message = "Plate must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\s]{5,10}$", message = "Plate must contain 5 to 10 alphanumeric characters")
    private String plate;

    /**
     * Brand of the vehicle.
     */
    @Column(nullable = false)
    @NotBlank(message = "Brand must not be blank")
    private String brand;

    /**
     * Model of the vehicle.
     */
    @Column(nullable = false)
    @NotBlank(message = "Model must not be blank")
    private String model;

    /**
     * Year the vehicle was registered.
     */
    @Column(nullable = false)
    @NotNull(message = "Registration year is required")
    private int registrationYear;

    /**
     * Driving licence required to operate the vehicle.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Required licence is required")
    private RequiredLicence requiredLicence;

    /**
     * Number of operators required to operate the vehicle during waste collection.
     */
    @Column(nullable = false)
    @NotNull(message = "Number of operators is required")
    private int requiredOperators;

    /**
     * Operational status of the vehicle.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vehicle status is required")
    private VehicleStatus vehicleStatus;

    /**
     * Date of last maintenance.
     */
    @Column(nullable = false)
    @NotNull(message = "Last maintenance date is required")
    private LocalDate lastMaintenanceDate;

    /**
     * Date of next scheduled maintenance.
     */
    @Column(nullable = false)
    @NotNull(message = "Next maintenance date is required")
    @FutureOrPresent(message = "Next maintenance date cannot be in the past")
    private LocalDate nextMaintenanceDate;

    /**
     * Default constructor.
     */
    public Vehicle() {
    }

    /**
     * Constructs a vehicle with all required attributes.
     *
     * @param plate             license plate
     * @param brand             vehicle brand
     * @param model             vehicle model
     * @param registrationYear  year of registration
     * @param requiredLicence   licence needed to drive
     * @param vehicleStatus     current vehicle status
     * @param requiredOperators number of operators needed for the vehicle
     */
    public Vehicle(final String plate, final String brand, final String model,
            final int registrationYear, final RequiredLicence requiredLicence,
            final VehicleStatus vehicleStatus, final int requiredOperators) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate must not be null");
        }
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.registrationYear = registrationYear;
        this.requiredLicence = requiredLicence;
        this.vehicleStatus = vehicleStatus;
        this.requiredOperators = requiredOperators;
        this.lastMaintenanceDate = LocalDate.now();
        this.nextMaintenanceDate = lastMaintenanceDate.plusYears(1);
    }

    @PrePersist
    @PreUpdate
    private void normalizePlate() {
        if (plate != null) {
            plate = plate.toUpperCase().trim();
        }
    }

    /**
     * @return vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }

    /**
     * @return license plate
     */
    public String getPlate() {
        return plate;
    }

    /**
     * @param plate the plate to set
     */
    public void setPlate(final String plate) {
        this.plate = plate;
    }

    /**
     * @return vehicle brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand the brand to set
     */
    public void setBrand(final String brand) {
        this.brand = brand;
    }

    /**
     * @return vehicle model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(final String model) {
        this.model = model;
    }

    /**
     * @return registration year
     */
    public int getRegistrationYear() {
        return registrationYear;
    }

    /**
     * @param registrationYear the registration year to set
     */
    public void setRegistrationYear(final int registrationYear) {
        this.registrationYear = registrationYear;
    }

    /**
     * @return required driving licence
     */
    public RequiredLicence getRequiredLicence() {
        return requiredLicence;
    }

    /**
     * @param requiredLicence the licence to set
     */
    public void setRequiredLicence(final RequiredLicence requiredLicence) {
        this.requiredLicence = requiredLicence;
    }

    /**
     * @return operational status
     */
    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    /**
     * @param vehicleStatus the status to set
     */
    public void setVehicleStatus(final VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    /**
     * @return number of operators required to operate the vehicle
     */
    public int getRequiredOperators() {
        return requiredOperators;
    }

    /**
     * @param requiredOperators number of operators needed for the vehicle
     */
    public void setRequiredOperators(final int requiredOperators) {
        this.requiredOperators = requiredOperators;
    }

    /**
     * @return date of last maintenance
     */
    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    /**
     * @param lastMaintenanceDate the last maintenance date
     */
    public void setLastMaintenanceDate(final LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    /**
     * @return date of next maintenance
     */
    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    /**
     * @param nextMaintenanceDate the next maintenance date
     */
    public void setNextMaintenanceDate(final LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    /**
     * Updates the vehicle's status.
     *
     * @param vehicleStatus the new status
     */
    public void updateStatus(final VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "Vehicle {Plate: %s, Brand: %s, Model: %s, Year: %d,%n"
                        + "RequiredLicence: %s, RequiredOperators: %d, Status: %s,%n"
                        + "LastMaint: %s, NextMaint: %s}",
                plate != null ? plate : "N/A", brand != null ? brand : "N/A",
                model != null ? model : "N/A", registrationYear,
                requiredLicence != null ? requiredLicence.name() : "N/A", requiredOperators,
                vehicleStatus != null ? vehicleStatus.name() : "N/A",
                lastMaintenanceDate != null ? lastMaintenanceDate.toString() : "N/A",
                nextMaintenanceDate != null ? nextMaintenanceDate.toString() : "N/A");
    }

    /**
     * Required driving licences for operating the vehicle.
     */
    public enum RequiredLicence {
        /**
         * Standard car licence.
         */
        B,
        /**
         * Light truck licence.
         */
        C1,
        /**
         * Heavy truck licence.
         */
        C
    }

    /**
     * Operational status of the vehicle.
     */
    public enum VehicleStatus {
        /**
         * Vehicle is in regular service.
         */
        IN_SERVICE,
        /**
         * Vehicle is under maintenance.
         */
        IN_MAINTENANCE,
        /**
         * Vehicle is not in use.
         */
        OUT_OF_SERVICE
    }
}
