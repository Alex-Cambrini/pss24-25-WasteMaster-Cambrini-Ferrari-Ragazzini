package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Vehicle;
import jakarta.persistence.EntityManager;
import java.util.List;

public class VehicleDAO extends GenericDAO<Vehicle> {

    public VehicleDAO(EntityManager entityManager) {
        super(entityManager, Vehicle.class);
    }

    public Vehicle findByPlate(String plate) {
        if (plate == null) {
            throw new IllegalArgumentException("Plate cannot be null");
        }
        return entityManager.find(Vehicle.class, plate.trim().toUpperCase());
    }

    public List<Vehicle> findByStatus(Vehicle.VehicleStatus status) {
        return entityManager.createQuery(
                "SELECT v FROM Vehicle v WHERE v.vehicleStatus = :status", Vehicle.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Vehicle> getAllVehicles() {
        return entityManager.createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }
}
