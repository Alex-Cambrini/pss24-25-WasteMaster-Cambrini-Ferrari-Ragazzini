package it.unibo.wastemaster.core.models;


import java.util.Date;
import java.util.List;


public class Route {
    private int id;
    private List<Location> stops;
    private Vehicle truck; 
    private RouteType type;

    public enum RouteType {
        ONE_TIME,
        RECURRING
    }

    public Route(int id, List<Location> stops, Vehicle truck, RouteType type) {
        this.id = id;
        this.stops = stops;
        this.truck = truck;
        this.type = type;
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

}


  

