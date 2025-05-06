package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.models.Route;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;

import java.util.*;


public class RouteManager {

    private Map<Integer, Route> routes; 
    private List<OneTimeSchedule> oneTimeSchedules;
    private List<RecurringSchedule> recurringSchedules;

    public RouteManager() {
        this.routes = new HashMap<>();
        this.oneTimeSchedules = new ArrayList<>();
        this.recurringSchedules = new ArrayList<>();
    }
  
    
    public void planRoute(Route route) {
        if (routes.containsKey(route.getId())) {
            throw new IllegalArgumentException("Route with this ID already exists.");
        }
        routes.put(route.getId(), route);
    }

    public Route getRoute(int id) {
        return routes.get(id);
    }

    public Collection<Route> getAllRoutes() {
        return routes.values();
    }

    public void removeRoute(int id) {
        routes.remove(id);
    }

    public void addOneTimeSchedule(OneTimeSchedule schedule) {
        oneTimeSchedules.add(schedule);
    }

    public void addRecurringSchedule(RecurringSchedule schedule) {
        recurringSchedules.add(schedule);
    }

    public List<OneTimeSchedule> getOneTimeSchedules() {
        return new ArrayList<>(oneTimeSchedules);
    }

    public List<RecurringSchedule> getRecurringSchedules() {
        return new ArrayList<>(recurringSchedules);
    }

    public void updateRecurringSchedule(int id, RecurringSchedule updatedSchedule) {
        for (int i = 0; i < recurringSchedules.size(); i++) {
            if (recurringSchedules.get(i).getScheduleId()==(id)) {
                recurringSchedules.set(i, updatedSchedule);
                return;
            }
        }
    }

    public void removeOneTimeSchedule(int id) {
            oneTimeSchedules.removeIf(schedule -> schedule.getScheduleId()==(id));
    }


}








