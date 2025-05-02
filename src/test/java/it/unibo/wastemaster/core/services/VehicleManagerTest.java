package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Vehicle;

public class VehicleManagerTest extends AbstractDatabaseTest {

    private VehicleManager vehicleManager;
    private Vehicle v1;
    private Vehicle v2;
    private Vehicle v3;

    @BeforeEach
    public void setUp() {
        super.setUp();
        vehicleManager = new VehicleManager(vehicleDAO);
        em.getTransaction().begin();

        v1 = new Vehicle("DD444DD", "Renault", "Master", 2023, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        v2 = new Vehicle("EE555EE", "MAN", "TGE", 2022, Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_MAINTENANCE);
        v3 = new Vehicle("FF666FF", "Ford", "Transit", 2021, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.OUT_OF_SERVICE);

        vehicleDAO.insert(v1);
        vehicleDAO.insert(v2);
        vehicleDAO.insert(v3);
    }

    @Test
    public void testAddVehicle() {
        LocalDate today = LocalDate.now();
        LocalDate nextYear = today.plusYears(1);

        Vehicle valid = new Vehicle("GG777GG", "VW", "Crafter", 2024, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        valid.setLastMaintenanceDate(today);
        valid.setNextMaintenanceDate(nextYear);

        Vehicle saved = vehicleManager.addVehicle(valid);
        assertNotNull(saved);
        assertEquals("VW", saved.getBrand());

        assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(null));

        Vehicle duplicate = new Vehicle("DD444DD", "Renault", "Master", 2023, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(duplicate));

        Vehicle withSpaces = new Vehicle("  DD444DD  ", "Brand", "Model", 2024, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(withSpaces));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> new Vehicle(null, "Brand", "Model", 2024,
                Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
        assertEquals("Plate must not be null", ex.getMessage());

        Vehicle emptyPlate = new Vehicle("", "Brand", "Model", 2024, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(emptyPlate));

        Vehicle manual = new Vehicle("AA000AA", "Test", "Manual", 2023, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        manual.setLastMaintenanceDate(today);
        manual.setNextMaintenanceDate(nextYear);
        Vehicle inserted = vehicleManager.addVehicle(manual);
        assertEquals(today, inserted.getLastMaintenanceDate());
        assertEquals(nextYear, inserted.getNextMaintenanceDate());
    }

    @Test
    public void testUpdateVehicle() {
        v1.setModel("Updated");
        vehicleManager.updateVehicle(v1);

        Vehicle updated = vehicleDAO.findByPlate("DD444DD");
        assertNotNull(updated);
        assertEquals("Updated", updated.getModel());

        assertThrows(IllegalArgumentException.class, () -> vehicleManager.updateVehicle(null));

        Vehicle ghost = new Vehicle("ZZ999ZZ", "Ghost", "Model", 2024, Vehicle.LicenceType.C1,
                Vehicle.VehicleStatus.IN_SERVICE);
        assertDoesNotThrow(() -> vehicleManager.updateVehicle(ghost));

        v1.setBrand(null);
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.updateVehicle(v1));
    }

    @Test
    public void testMarkMaintenanceAsComplete() {
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, v2.getVehicleStatus());

        vehicleManager.markMaintenanceAsComplete(v2);
        Vehicle updated = vehicleDAO.findByPlate("EE555EE");

        assertNotNull(updated);
        assertEquals(Vehicle.VehicleStatus.IN_SERVICE, updated.getVehicleStatus());
        assertEquals(LocalDate.now(), updated.getLastMaintenanceDate());
        assertEquals(LocalDate.now().plusYears(1), updated.getNextMaintenanceDate());

        assertThrows(IllegalArgumentException.class, () -> vehicleManager.markMaintenanceAsComplete(null));
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.markMaintenanceAsComplete(v1));
    }

    @Test
    public void testCanOperateVehicle() {
        assertTrue(vehicleManager.canOperateVehicle(v1, Arrays.asList(Vehicle.LicenceType.C1)));
        assertTrue(vehicleManager.canOperateVehicle(v1, Arrays.asList(Vehicle.LicenceType.C)));
        assertFalse(vehicleManager.canOperateVehicle(v2, Arrays.asList(Vehicle.LicenceType.C1)));
        assertTrue(vehicleManager.canOperateVehicle(v2, Arrays.asList(Vehicle.LicenceType.C)));

        assertThrows(IllegalArgumentException.class,
                () -> vehicleManager.canOperateVehicle(null, Arrays.asList(Vehicle.LicenceType.C1)));
        assertThrows(IllegalArgumentException.class, () -> vehicleManager.canOperateVehicle(v1, null));
        assertThrows(IllegalArgumentException.class,
                () -> vehicleManager.canOperateVehicle(v1, Collections.emptyList()));
    }
}
