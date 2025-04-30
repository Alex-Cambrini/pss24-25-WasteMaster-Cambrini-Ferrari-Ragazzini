package it.unibo.wastemaster.core.models;


import java.util.Date;
import java.util.List;


public class Route {
    private int id;
    private List<Location> stops;
    private Vehicle truck; 
    private Schedule schedule;

   

    public Route(int id, List<Location> stops, Vehicle truck, Schedule schedule) {
        this.id = id;
        this.stops = stops;
        this.truck = truck;
        this.schedule = schedule;
    }

    
    public int getId() {
        return id;
    }

    public List<Location> getStops() {
        return stops;
    }

    public void setStops(List<Location> stops) {
        this.stops = stops;
    }

    public Vehicle getTruck() {
        return truck;
    }

    public void setTruck(Vehicle truck) {
        this.truck = truck;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}


  

