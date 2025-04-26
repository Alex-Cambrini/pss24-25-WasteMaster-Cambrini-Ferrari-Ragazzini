package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;

public class VehicleTest extends AbstractDatabaseTest {

    private Vehicle vehicle;

	@BeforeEach
	public void setUp() {
		super.setUp();
		vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
	}

    @Test
	public void testVehicleGettersAndSetters() {
		vehicle.setBrand("Mercedes");
		vehicle.setModel("Sprinter");
		vehicle.setRegistrationYear(2022);
		vehicle.setLicenceType(Vehicle.LicenceType.C);
		vehicle.setVehicleStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
		LocalDate newDate = LocalDate.of(2024, 1, 1);
		vehicle.setLastMaintenanceDate(newDate);

		assertEquals("Mercedes", vehicle.getBrand());
		assertEquals("Sprinter", vehicle.getModel());
		assertEquals(2022, vehicle.getRegistrationYear());
		assertEquals(Vehicle.LicenceType.C, vehicle.getLicenceType());
		assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, vehicle.getVehicleStatus());
		assertEquals(newDate, vehicle.getLastMaintenanceDate());
		assertEquals(2, vehicle.getCapacity()); // perché C -> 2
	}

}
