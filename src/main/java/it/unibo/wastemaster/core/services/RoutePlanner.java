package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.models.Route;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;

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
