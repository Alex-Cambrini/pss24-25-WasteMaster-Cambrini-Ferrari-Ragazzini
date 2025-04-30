package it.unibo.wastemaster.core.models;


import java.util.*;


public class RoutePlanner {

    private Map<Long, Route> routes; 
    private List<OneTimeSchedule> oneTimeSchedules;
    private List<RecurringSchedule> recurringSchedules;

    public RoutePlanner() {
        this.routes = new HashMap<>();
        this.oneTimeSchedules = new ArrayList<>();
        this.recurringSchedules = new ArrayList<>();
    }
  
    

}
