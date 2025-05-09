package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Vehicle;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
public class TripManager {

    private final TripDAO tripDAO;

    public TripManager(EntityManager entityManager) {
        this.tripDAO = new TripDAO(entityManager);
    }


    
}



