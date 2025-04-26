package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleDAOTest extends AbstractDatabaseTest {

    private Vehicle v1;
    private Vehicle v2;
    private Vehicle v3;

    @BeforeEach
    public void setUp() {
        super.setUp();

        em.getTransaction().begin();

        v1 = new Vehicle("AA111AA", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
        v2 = new Vehicle("BB222BB", "Mercedes", "Sprinter", 2021, Vehicle.LicenceType.C,
                Vehicle.VehicleStatus.IN_MAINTENANCE);
        v3 = new Vehicle("CC333CC", "Fiat", "Ducato", 2022, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.OUT_OF_SERVICE);

        vehicleDAO.insert(v1);
        vehicleDAO.insert(v2);
        vehicleDAO.insert(v3);
    }

    @Test
    public void testFindByPlate() {
        Vehicle found = vehicleDAO.findByPlate("AA111AA");
        assertNotNull(found);
        assertEquals("Iveco", found.getBrand());
        assertEquals(Vehicle.LicenceType.C1, found.getLicenceType());

        Vehicle found2 = vehicleDAO.findByPlate("BB222BB");
        assertNotNull(found2);
        assertEquals("Mercedes", found2.getBrand());
        assertEquals(Vehicle.LicenceType.C, found2.getLicenceType());
    }

    @Test
    public void testFindByPlateNotFound() {
        Vehicle found = vehicleDAO.findByPlate("ZZ999ZZ");
        assertNull(found);
    }

    @Test
    public void testFindByStatus() {
        List<Vehicle> inService = vehicleDAO.findByStatus(Vehicle.VehicleStatus.IN_SERVICE);
        assertEquals(1, inService.size());
        assertEquals("AA111AA", inService.get(0).getPlate());
        assertEquals(Vehicle.VehicleStatus.IN_SERVICE, inService.get(0).getVehicleStatus());

        List<Vehicle> inMaintenance = vehicleDAO.findByStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
        assertEquals(1, inMaintenance.size());
        assertEquals("BB222BB", inMaintenance.get(0).getPlate());
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, inMaintenance.get(0).getVehicleStatus());

        List<Vehicle> outOfService = vehicleDAO.findByStatus(Vehicle.VehicleStatus.OUT_OF_SERVICE);
        assertEquals(1, outOfService.size());
        assertEquals("CC333CC", outOfService.get(0).getPlate());
        assertEquals(Vehicle.VehicleStatus.OUT_OF_SERVICE, outOfService.get(0).getVehicleStatus());
    }

    @Test
    public void testGetAllVehicles() {
        List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        assertEquals(3, allVehicles.size());
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals("AA111AA")));
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals("BB222BB")));
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals("CC333CC")));
    }
}
