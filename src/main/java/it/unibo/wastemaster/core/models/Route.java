package it.unibo.wastemaster.core.models;


import java.util.Date;
import java.util.List;


public class Route {

    private int id;
    private Date departureDate;
    private List<Location> stops;
    private double estimatedDuration;
    private String status;
    private Schedule schedule;  

    public Route(int id, Date departureDate, List<Location> stops, double estimatedDuration, Schedule schedule) {
        this.id = id;
        this.departureDate = departureDate;
        this.stops = stops;
        this.estimatedDuration = estimatedDuration;
        this.status = "In Progress";
        this.schedule = schedule;
    }

}
  

