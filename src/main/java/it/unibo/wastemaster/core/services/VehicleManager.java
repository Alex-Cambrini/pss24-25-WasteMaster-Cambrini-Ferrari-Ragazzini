package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class VehicleManager {
    private final EntityManager em;
	private final GenericDAO<Vehicle> vehicleDAO;

	public VehicleManager(EntityManagerFactory emf) {
		this.em = emf.createEntityManager();
		this.vehicleDAO = new GenericDAO<>(em, Vehicle.class);
	}
}
