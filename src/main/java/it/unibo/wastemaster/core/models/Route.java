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

   

}
  

