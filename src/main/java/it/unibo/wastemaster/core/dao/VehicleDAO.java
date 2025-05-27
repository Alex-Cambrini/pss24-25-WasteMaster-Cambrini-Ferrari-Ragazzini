package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * DAO for managing Vehicle entities.
 */
public final class VehicleDAO extends GenericDAO<Vehicle> {

    /**
     * Constructs a VehicleDAO with the given entity manager.
     * 
     * @param entityManager the EntityManager to use
     */
    public VehicleDAO(final EntityManager entityManager) {
        super(entityManager, Vehicle.class);
    }

    /**
     * Finds a vehicle by its plate.
     * 
     * @param plate the vehicle plate
     * @return the Vehicle if found, null otherwise
     */
    public Vehicle findByPlate(final String plate) {
        try {
            return entityManager
                    .createQuery("SELECT v FROM Vehicle v WHERE v.plate = :plate",
                            Vehicle.class)
                    .setParameter("plate", plate).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Retrieves all vehicles with the given status.
     * 
     * @param status the vehicle status
     * @return list of vehicles matching the status
     */
    public List<Vehicle> findByStatus(final Vehicle.VehicleStatus status) {
        return entityManager
                .createQuery("SELECT v FROM Vehicle v WHERE v.vehicleStatus = :status",
                        Vehicle.class)
                .setParameter("status", status).getResultList();
    }

    /**
     * Retrieves all vehicles.
     * 
     * @return list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return entityManager.createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }

    /**
     * Retrieves all vehicle details.
     * 
     * @return list of all vehicles with full details
     */
    public List<Vehicle> findVehicleDetails() {
        return entityManager.createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }
}
