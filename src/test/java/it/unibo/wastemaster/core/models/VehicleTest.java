package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.*;

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
        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
    }

    @Test
    public void testVehicleGettersAndSetters() {
        vehicle.setBrand("Mercedes");
        vehicle.setModel("Sprinter");
        vehicle.setRegistrationYear(2022);
        vehicle.setRequiredLicence(Vehicle.RequiredLicence.C);
        vehicle.setVehicleStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
        vehicle.setCapacity(2);
        LocalDate newDate = LocalDate.of(2024, 1, 1);
        LocalDate nextMaintaDate = LocalDate.of(2025, 1, 1);

        vehicle.setLastMaintenanceDate(newDate);
        vehicle.setNextMaintenanceDate(nextMaintaDate);

        assertEquals("Mercedes", vehicle.getBrand());
        assertEquals("Sprinter", vehicle.getModel());
        assertEquals(2022, vehicle.getRegistrationYear());
        assertEquals(Vehicle.RequiredLicence.C, vehicle.getRequiredLicence());
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, vehicle.getVehicleStatus());
        assertEquals(newDate, vehicle.getLastMaintenanceDate());
        assertEquals(nextMaintaDate, vehicle.getNextMaintenanceDate());
        assertEquals(2, vehicle.getCapacity());
    }

    @Test
    public void testValidVehicle() {
        Set<ConstraintViolation<Vehicle>> violations = ValidateUtils.VALIDATOR.validate(vehicle);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testVehicleValidation() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new Vehicle(null, "Iveco", "Daily", 2020, Vehicle.RequiredLicence.C1,
                    Vehicle.VehicleStatus.IN_SERVICE, 3);
        });
        assertEquals("Plate must not be null", ex.getMessage());

        Set<ConstraintViolation<Vehicle>> violations;

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("", "Iveco", "Daily", 2020, Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE,
                        3));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", null, "Daily", 2020, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, 3));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "", "Daily", 2020, Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE,
                        3));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "Iveco", null, 2020, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, 3));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "Iveco", "", 2020, Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE,
                        3));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testVehiclePersistence() {
        vehicleDAO.insert(vehicle);

        Vehicle found = vehicleDAO.findByPlate(vehicle.getPlate());
        int foundID = found.getVehicleId();

        assertNotNull(found);
        assertEquals(vehicle.getBrand(), found.getBrand());
        assertEquals(vehicle.getModel(), found.getModel());
        assertEquals(vehicle.getRequiredLicence(), found.getRequiredLicence());
        assertEquals(vehicle.getLastMaintenanceDate(), found.getLastMaintenanceDate());
        assertEquals(vehicle.getNextMaintenanceDate(), found.getNextMaintenanceDate());

        vehicleDAO.delete(found);

        Vehicle deleted = vehicleDAO.findById(foundID);
        assertNull(deleted);
    }

    @Test
    public void testUpdateStatus() {
        vehicle.updateStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, vehicle.getVehicleStatus());
    }

    @Test
    public void testLastMaintenanceDateDefault() {
        Vehicle newVehicle = new Vehicle("CD456EF", "Mercedes", "Sprinter", 2021,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, 2);
        LocalDate today = LocalDate.now();
        assertEquals(today, newVehicle.getLastMaintenanceDate());
    }

    @Test
    public void testNextMaintenanceDateDefault() {
        Vehicle newVehicle = new Vehicle("CD456EF", "Mercedes", "Sprinter", 2021,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, 2);
        LocalDate expected = LocalDate.now().plusYears(1);
        assertEquals(expected, newVehicle.getNextMaintenanceDate());
    }

    @Test
    public void testToString() {
        String output = vehicle.toString();
        assertNotNull(output);
        assertTrue(output.contains("Vehicle"));
        assertTrue(output.contains(vehicle.getPlate()));
        assertTrue(output.contains(vehicle.getBrand()));
        assertTrue(output.contains(vehicle.getModel()));
    }
}
