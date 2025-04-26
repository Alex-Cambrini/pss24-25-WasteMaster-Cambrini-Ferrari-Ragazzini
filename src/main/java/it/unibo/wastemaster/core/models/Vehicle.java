package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Vehicle {

    @Id
    @Column(length = 10, nullable = false)
    @NotBlank(message = "Plate must not be blank")
    private String plate;

    @Column(nullable = false)
    @NotBlank(message = "Brand must not be blank")
    @NotNull(message = "brand is required")
    private String brand;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be blank")
    @NotNull(message = "model is required")
    private String model;

    @Column(nullable = false)
    @NotNull(message = "Registration year is required")
    private int registrationYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Licence type is required")
    private LicenceType licenceType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Vehicle status is required")
    private VehicleStatus vehicleStatus;

    public enum LicenceType {
        C1(3),
        C(2);

        private final int capacity;

        LicenceType(int capacity) {
            this.capacity = capacity;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public enum VehicleStatus {
        IN_SERVICE,
        IN_MAINTENANCE,
        OUT_OF_SERVICE
    }

    public Vehicle() {
    }

    public Vehicle(String plate, String brand, String model, int registrationYear,
            LicenceType licenceType, VehicleStatus vehicleStatus) {
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.registrationYear = registrationYear;
        this.licenceType = licenceType;
        this.vehicleStatus = vehicleStatus;
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

    public LicenceType getLicenceType() {
        return licenceType;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public int getCapacity() {
        return licenceType.getCapacity();
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
                "Vehicle Info: Brand: %s, Model: %s, Registration year: %d, Plate: %s, Licence: %s, Capacity: %d persons, Status: %s",
                brand, model, registrationYear, plate, licenceType, getCapacity(), vehicleStatus);
    }
}