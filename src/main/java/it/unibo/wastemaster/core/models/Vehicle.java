package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Vehicle {
    
    @Id
    @Column(length = 10, nullable = false)
    @NotBlank(message = "Plate must not be blank")
    private String plate;

    @Column(nullable = false)
    @Min(value = 1, message = "Capacity must be greater than 0")
    @NotNull(message = "capacity is required")
    private int capacity;

    @Column(nullable = false)
    @NotBlank(message = "Brand must not be blank")
    @NotNull(message = "brand is required")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be blank")
    @NotNull(message = "model is required")
    private String model;

    @Column(nullable = false)
    @NotNull(message = "year is required")
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Licence type is required")
    private LicenceType licenceType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vehicle status is required")
    private VehicleStatus vehicleStatus;

    public enum LicenceType {
        C1, // Fino a 3.5 t
        C, // Oltre 3.5 t
    }

    public enum VehicleStatus {
        IN_SERVICE,
        IN_MAINTENANCE,
        OUT_OF_SERVICE
    }

    public Vehicle() {
    }

    public Vehicle(String plate, int capacity, String brand, String model, int year, LicenceType licenceType,
            VehicleStatus vehicleStatus) {
        this.plate = plate;
        this.capacity = capacity;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licenceType = licenceType;
        this.vehicleStatus = vehicleStatus;
    }

    public String getPlate() {
        return plate;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public LicenceType getLicenceType() {
        return licenceType;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setLicenceType(LicenceType licenceType) {
        this.licenceType = licenceType;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public void updateStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getInfo() {
        return String.format(
                "Vehicle Info: Brand: %s, Model: %s, Year: %d, Plate: %s, Licence: %s, Status: %s",
                brand, model, year, plate, licenceType, vehicleStatus);
    }
}