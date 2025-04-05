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

    public Vehicle addVehicle(String plate, int capacity, String brand, String model, int year,
							  Vehicle.LicenceType licenceType, Vehicle.VehicleStatus status) {
		Vehicle vehicle = new Vehicle(plate, capacity, brand, model, year, licenceType, status);
		vehicleDAO.insert(vehicle);
		return vehicle;
	}

    public Vehicle getVehicleByPlate(String plate) {
		return em.find(Vehicle.class, plate);
	}
}
