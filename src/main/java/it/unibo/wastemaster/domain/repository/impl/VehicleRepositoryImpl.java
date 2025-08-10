package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.VehicleRepository;
import it.unibo.wastemaster.infrastructure.dao.VehicleDAO;
import java.util.Optional;

public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleDAO vehicleDAO;

    public VehicleRepositoryImpl(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    @Override
    public void save(Vehicle vehicle) {
        vehicleDAO.insert(vehicle);
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return Optional.ofNullable(vehicleDAO.findByPlate(plate));
    }

    @Override
    public void update(Vehicle vehicle) {
        vehicleDAO.update(vehicle);
    }

    @Override
    public void delete(Vehicle vehicle) {
        vehicleDAO.delete(vehicle);
    }
}
