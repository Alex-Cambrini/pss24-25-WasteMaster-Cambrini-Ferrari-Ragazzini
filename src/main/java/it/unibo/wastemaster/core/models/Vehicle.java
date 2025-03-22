package it.unibo.wastemaster.core.models;

public class Vehicle {
    private int id;
    private String type;
    private String status;

    // Constructor for Vehicle
    public Vehicle(int id, String type, String status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }

    // add get for the attributes of the class Vehicle
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    // add set for the attributes of the class Vehicle
    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // add updateStatus method to update the status of the vehicle
    public void updateStatus(String status) {
        this.status = status;
    }
}
