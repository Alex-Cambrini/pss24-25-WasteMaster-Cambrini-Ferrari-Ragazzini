package it.unibo.wastemaster.core.models;

import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.Vehicle.VehicleStatus;


public class Trip {
     private int id;
    private List<Location> stops;
    private Vehicle vehicle; 
    private Squad squad;
    private LocalDate date;
    private List<Collection> collections;
    private String zone;
    private boolean vehicleAssigned;
    private boolean squadAssigned;


    public Trip() {
    }

    public Trip(int id, List<Location> stops, Vehicle vehicle, Squad squad, LocalDate date, List<Collection> collections, String zone) {
        this.id = id;
        this.stops = stops;
        this.vehicle = vehicle;
        this.squad = squad;
        this.date = date;
        this.collections = collections;
        this.zone = zone;
        this.vehicleAssigned = false;
        this.squadAssigned = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Location> getStops() {
        return stops;
    }

    public void setStops(List<Location> stops) {
        this.stops = stops;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Squad getSquad() {
        return squad;
    }

    public void setSquad(Squad squad) {
        this.squad = squad;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public boolean isVehicleAssigned() {
        return vehicleAssigned;
    }

    public void setVehicleAssigned(boolean vehicleAssigned) {
        this.vehicleAssigned = vehicleAssigned;
    }

    public boolean isSquadAssigned() {
        return squadAssigned;
    }

    public void setSquadAssigned(boolean squadAssigned) {
        this.squadAssigned = squadAssigned;
    }



    public boolean assignVehicle(Vehicle vehicle) {
        if (vehicleAssigned) {
            return false; // Veicolo già assegnato
        }
        if (vehicle.getVehicleStatus() == VehicleStatus.IN_SERVICE) {
            this.vehicle = vehicle;
            this.vehicleAssigned = true;
            return true;
        }
        return false; // Veicolo non in servizio
    }

    // Metodo per assegnare una squadra alla rotta
    public boolean assignSquad(Squad squad) {
        if (!squadAssigned && squad != null) {
            this.squad = squad;
            this.squadAssigned = true;
            return true;
        }
        return false; // Squadra già assegnata o squadra nulla
    }

    public boolean assignVehicleDynamically(List<Vehicle> availableVehicles) {
        for (Vehicle vehicle : availableVehicles) {
            // Verifica se il veicolo è disponibile (in servizio)
            if (vehicle.getVehicleStatus() == VehicleStatus.IN_SERVICE) {
                this.vehicle = vehicle;
                this.vehicleAssigned = true;
                return true;
            }
        }
        return false; // Nessun veicolo disponibile o nessun veicolo in servizio
    }

    // Metodo per annullare una collezione
    public void cancelCollection(Collection collection) {
        if (this.collections.contains(collection)) {
            collection.setCollectionStatus(CollectionStatus.CANCELLED);
        }
    }

    // Metodo per aggiungere una collezione alla rotta
    public void addCollection(Collection collection) {
        this.collections.add(collection);
    }

    // Metodo per rimuovere una collezione dalla rotta
    public void removeCollection(Collection collection) {
        this.collections.remove(collection);
    }

    @Override
    public String toString() {
        return String.format("Route ID: %d, Date: %s, Zone: %s, Vehicle: %s, Squad: %s, Stops: %d, Collections: %d, Vehicle Assigned: %b, Squad Assigned: %b",
                id, date, zone, vehicle != null ? vehicle.getPlate() : "None", squad != null ? squad.getSquadName() : "None", stops.size(), collections.size(), vehicleAssigned, squadAssigned);
    }
}
  

