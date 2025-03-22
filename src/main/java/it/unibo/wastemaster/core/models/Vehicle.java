package it.unibo.wastemaster.core.models;

public class Vehicle {
    private int capacity;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private LicenceType licenceType;
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

    public Vehicle(int capacity, String brand, String model, int year, String plate, LicenceType licenceType, VehicleStatus vehicleStatus) {
        this.capacity = capacity;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.licenceType = licenceType;
        this.vehicleStatus = vehicleStatus;
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

    public String getPlate() {
        return plate;
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

    public void setPlate(String plate) {
        this.plate = plate;
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



//TEST
    // public static void main(String[] args) {
    //     Vehicle vehicle = new Vehicle(1000, "BrandX", "ModelY", 2020, "AB123CD", Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_SERVICE);

    //     if (vehicle.getCapacity() == 1000 && vehicle.getBrand().equals("BrandX") && vehicle.getModel().equals("ModelY")
    //             && vehicle.getYear() == 2020 && vehicle.getPlate().equals("AB123CD") && vehicle.getLicenceType() == Vehicle.LicenceType.C
    //             && vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_SERVICE) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     vehicle.updateStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
    //     if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_MAINTENANCE) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     String info = vehicle.getInfo();
    //     if (info.contains("BrandX") && info.contains("ModelY") && info.contains("2020") && info.contains("AB123CD")
    //             && info.contains("C") && info.contains("IN_MAINTENANCE")) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

    //     vehicle.updateStatus(Vehicle.VehicleStatus.OUT_OF_SERVICE);
    //     if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.OUT_OF_SERVICE) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }
    // }

}
