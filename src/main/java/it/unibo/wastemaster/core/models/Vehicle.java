package it.unibo.wastemaster.core.models;

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

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vehicleId;

    @Column(length = 10, nullable = false, unique = true)
    @NotBlank(message = "Plate must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\s]{5,10}$", message = "Plate must contain 5 to 10 alphanumeric characters (letters, digits, dashes or spaces)")
    private String plate;

    @Column(nullable = false)
    @NotBlank(message = "Brand must not be blank")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be blank")
    private String model;

    @Column(nullable = false)
    @NotNull(message = "Registration year is required")
    private int registrationYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Required licence is required")
    private RequiredLicence requiredLicence;

    @Column(nullable = false)
    @NotNull(message = "Vehicle capacity is required")
    private int capacity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vehicle status is required")
    private VehicleStatus vehicleStatus;

    @Column(nullable = false)
    @NotNull(message = "Last maintenance date is required")
    private LocalDate lastMaintenanceDate;

    @Column(nullable = false)
    @NotNull(message = "Next maintenance date is required")
    @FutureOrPresent(message = "Next maintenance date cannot be in the past")
    private LocalDate nextMaintenanceDate;

    public enum RequiredLicence {
        B,
        C1,
        C
    }

    public enum VehicleStatus {
        IN_SERVICE,
        IN_MAINTENANCE,
        OUT_OF_SERVICE
    }

    public Vehicle() {
    }

    public Vehicle(String plate, String brand, String model, int registrationYear,
            RequiredLicence requiredLicence, VehicleStatus vehicleStatus, int capacity) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate must not be null");
        }
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.registrationYear = registrationYear;
        this.requiredLicence = requiredLicence;
        this.vehicleStatus = vehicleStatus;
        this.capacity = capacity;
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

    public int getVehicleId() {
        return vehicleId;
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

    public RequiredLicence getRequiredLicence() {
        return requiredLicence;
    }

    public void setRequiredLicence(RequiredLicence requiredLicence) {
        this.requiredLicence = requiredLicence;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRegistrationYear(int registrationYear) {
        this.registrationYear = registrationYear;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public void updateStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "Vehicle {Plate: %s, Brand: %s, Model: %s, Year: %d, RequiredLicence: %s, Capacity: %d, Status: %s, LastMaint: %s, NextMaint: %s}",
                plate != null ? plate : "N/A",
                brand != null ? brand : "N/A",
                model != null ? model : "N/A",
                registrationYear,
                requiredLicence != null ? requiredLicence.name() : "N/A",
                capacity,
                vehicleStatus != null ? vehicleStatus.name() : "N/A",
                lastMaintenanceDate != null ? lastMaintenanceDate.toString() : "N/A",
                nextMaintenanceDate != null ? nextMaintenanceDate.toString() : "N/A");
    }
}
