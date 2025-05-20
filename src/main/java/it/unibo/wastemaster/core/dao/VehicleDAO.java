package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class VehicleDAO extends GenericDAO<Vehicle> {

    public VehicleDAO(EntityManager entityManager) {
        super(entityManager, Vehicle.class);
    }

    public Vehicle findByPlate(String plate) {
        try {
            return entityManager.createQuery(
                    "SELECT v FROM Vehicle v WHERE v.plate = :plate", Vehicle.class)
                    .setParameter("plate", plate)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
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

    public List<Object[]> findVehicleDetails() {
        return entityManager.createQuery("""
                	SELECT v.plate, v.brand, v.model, v.registrationYear,
                		   v.requiredLicence, v.vehicleStatus,
                		   v.lastMaintenanceDate, v.nextMaintenanceDate
                	FROM Vehicle v
                """, Object[].class).getResultList();
    }
}
