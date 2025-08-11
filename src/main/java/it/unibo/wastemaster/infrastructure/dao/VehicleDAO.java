package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

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
     * @return an Optional containing the Vehicle if found, or an empty Optional otherwise
     */
    public Optional<Vehicle> findByPlate(final String plate) {
        try {
            Vehicle vehicle = getEntityManager()
                    .createQuery("SELECT v FROM Vehicle v WHERE v.plate = :plate",
                            Vehicle.class)
                    .setParameter("plate", plate)
                    .getSingleResult();
            return Optional.of(vehicle);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves all vehicles with the given status.
     *
     * @param status the vehicle status
     * @return list of vehicles matching the status
     */
    public List<Vehicle> findByStatus(final Vehicle.VehicleStatus status) {
        return getEntityManager()
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
        return getEntityManager().createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }

    /**
     * Retrieves all vehicle details.
     *
     * @return list of all vehicles with full details
     */
    public List<Vehicle> findVehicleDetails() {
        return getEntityManager().createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }
}
