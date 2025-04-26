package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

		v1 = new Vehicle("DD444DD", "Renault", "Master", 2023, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
		v2 = new Vehicle("EE555EE", "MAN", "TGE", 2022, Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_MAINTENANCE);
		v3 = new Vehicle("FF666FF", "Ford", "Transit", 2021, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.OUT_OF_SERVICE);

		vehicleDAO.insert(v1);
		vehicleDAO.insert(v2);
		vehicleDAO.insert(v3);
	}

    @Test
	public void testAddVehicleSuccess() {
		Vehicle newVehicle = vehicleManager.addVehicle("GG777GG", "Volkswagen", "Crafter", 2024,
				Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
		assertNotNull(newVehicle);
		assertEquals("Volkswagen", newVehicle.getBrand());
	}

	@Test
	public void testAddVehicleDuplicatePlate() {
		assertThrows(IllegalArgumentException.class, () -> {
			vehicleManager.addVehicle("DD444DD", "Fake", "Fake", 2023, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
		});
	}

    @Test
	public void testUpdateStatus() {
		vehicleManager.updateStatus("DD444DD", Vehicle.VehicleStatus.IN_MAINTENANCE);
		Vehicle updated = vehicleDAO.findByPlate("DD444DD");
		assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, updated.getVehicleStatus());
	}

	@Test
	public void testUpdateStatusInvalidPlate() {
		assertThrows(IllegalArgumentException.class, () -> {
			vehicleManager.updateStatus("ZZ999ZZ", Vehicle.VehicleStatus.IN_SERVICE);
		});
	}

    @Test
	public void testCanOperateVehicle() {
		List<Vehicle.LicenceType> licences = List.of(Vehicle.LicenceType.C1, Vehicle.LicenceType.C);

		assertTrue(vehicleManager.canOperateVehicle(v1, licences));
		assertTrue(vehicleManager.canOperateVehicle(v2, List.of(Vehicle.LicenceType.C)));
		assertFalse(vehicleManager.canOperateVehicle(v2, List.of(Vehicle.LicenceType.C1)));
	}
}
