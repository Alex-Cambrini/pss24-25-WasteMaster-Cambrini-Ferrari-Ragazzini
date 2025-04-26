package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

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
		assertEquals(2, vehicle.getCapacity());
	}

    @Test
	public void testValidVehicle() {
		Set<ConstraintViolation<Vehicle>> violations = ValidateUtils.VALIDATOR.validate(vehicle);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testVehicleValidation() {
		Set<ConstraintViolation<Vehicle>> violations;

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle(null, "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle("", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle("AB123CD", null, "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle("AB123CD", "", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle("AB123CD", "Iveco", null, 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);

		violations = ValidateUtils.VALIDATOR.validate(new Vehicle("AB123CD", "Iveco", "", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE));
		assertTrue(violations.size() > 0);
	}

    @Test
	public void testVehiclePersistence() {
		vehicleDAO.insert(vehicle);

		Vehicle found = vehicleDAO.findByPlate(vehicle.getPlate());
		assertNotNull(found);
		assertEquals(vehicle.getBrand(), found.getBrand());
		assertEquals(vehicle.getModel(), found.getModel());
		assertEquals(vehicle.getLicenceType(), found.getLicenceType());

		vehicleDAO.delete(vehicle);

		Vehicle deleted = vehicleDAO.findByPlate(vehicle.getPlate());
		assertNull(deleted);
	}

	@Test
	public void testGetInfo() {
		String expectedInfo = String.format(
			"Vehicle Info: Brand: %s, Model: %s, Registration year: %d, Plate: %s, Licence: %s, Capacity: %d persons, Status: %s, Last Maintenance: %s",
			"Iveco",
			"Daily",
			2020,
			"AB123CD",
			Vehicle.LicenceType.C1,
			3,
			Vehicle.VehicleStatus.IN_SERVICE,
			vehicle.getLastMaintenanceDate()
		);

		assertEquals(expectedInfo, vehicle.getInfo());
	}
}
