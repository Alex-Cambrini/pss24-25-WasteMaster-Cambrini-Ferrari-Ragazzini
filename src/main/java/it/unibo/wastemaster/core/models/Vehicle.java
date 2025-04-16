package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
@Entity
public class Vehicle {
    
    @Id
    private String plate;
    private int capacity;
    private String brand;
    private String model;
    private int year;
    @Enumerated(EnumType.STRING)
    private LicenceType licenceType;

    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    public enum LicenceType {
        C1,    // Patente per veicoli con massa fino a 3.5 t
        C,     // Patente per veicoli con massa oltre 3.5 t
        C1E,   // Patente per veicoli con massa oltre 3.5 t + rimorchio
        CE     // Patente per veicoli con massa oltre 3.5 t + rimorchio pesante
    }

    public enum VehicleStatus {
        IN_SERVICE,
        IN_MAINTENANCE,
        OUT_OF_SERVICE
    }

    public Vehicle() {}

    // Costruttore
    public Vehicle(String plate, int capacity, String brand, String model, int year, LicenceType licenceType, VehicleStatus vehicleStatus) {
        this.plate = plate;
        this.capacity = capacity;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licenceType = licenceType;
        this.vehicleStatus = vehicleStatus;
    }

    // Getter
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

    // Setter
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
        return String.format("Vehicle Info: Brand: %s, Model: %s, Year: %d, Plate: %s, Licence: %s, Status: %s",
                brand, model, year, plate, licenceType, vehicleStatus);
    }

}