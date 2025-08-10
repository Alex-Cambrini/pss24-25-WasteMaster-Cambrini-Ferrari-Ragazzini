package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Vehicle;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Vehicle entity.
 */
class VehicleTest extends AbstractDatabaseTest {

    private Vehicle vehicle;

    @Override
    @BeforeEach
    public void setUp() {
        final int defaultYear = 2020;
        final int defaultCapacity = 3;
        super.setUp();
        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", defaultYear,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE,
                defaultCapacity);
    }

    @Test
    void testVehicleGettersAndSetters() {
        final int updatedYear = 2022;
        final int updatedCapacity = 2;
        final LocalDate lastMaintenance = LocalDate.of(2024, 1, 1);
        final LocalDate nextMaintenance = LocalDate.of(2025, 1, 1);

        vehicle.setBrand("Mercedes");
        vehicle.setModel("Sprinter");
        vehicle.setRegistrationYear(updatedYear);
        vehicle.setRequiredLicence(Vehicle.RequiredLicence.C);
        vehicle.setVehicleStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
        vehicle.setCapacity(updatedCapacity);
        vehicle.setLastMaintenanceDate(lastMaintenance);
        vehicle.setNextMaintenanceDate(nextMaintenance);

        assertEquals("Mercedes", vehicle.getBrand());
        assertEquals("Sprinter", vehicle.getModel());
        assertEquals(updatedYear, vehicle.getRegistrationYear());
        assertEquals(Vehicle.RequiredLicence.C, vehicle.getRequiredLicence());
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, vehicle.getVehicleStatus());
        assertEquals(lastMaintenance, vehicle.getLastMaintenanceDate());
        assertEquals(nextMaintenance, vehicle.getNextMaintenanceDate());
        assertEquals(updatedCapacity, vehicle.getCapacity());
    }

    @Test
    void testValidVehicle() {
        Set<ConstraintViolation<Vehicle>> violations =
                ValidateUtils.VALIDATOR.validate(vehicle);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testVehicleValidation() {
        final int year = 2020;
        final int capacity = 3;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new Vehicle(null, "Iveco", "Daily", year, Vehicle.RequiredLicence.C1,
                    Vehicle.VehicleStatus.IN_SERVICE, capacity);
        });
        assertEquals("Plate must not be null", ex.getMessage());

        Set<ConstraintViolation<Vehicle>> violations;

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("", "Iveco", "Daily", year, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, capacity));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", null, "Daily", year, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, capacity));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "", "Daily", year, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, capacity));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "Iveco", null, year, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, capacity));
        assertFalse(violations.isEmpty());

        violations = ValidateUtils.VALIDATOR.validate(
                new Vehicle("AB123CD", "Iveco", "", year, Vehicle.RequiredLicence.C1,
                        Vehicle.VehicleStatus.IN_SERVICE, capacity));
        assertFalse(violations.isEmpty());
    }

    @Test
    void testVehiclePersistence() {
        getVehicleDAO().insert(vehicle);

        Vehicle found = getVehicleDAO().findByPlate(vehicle.getPlate());
        int id = found.getVehicleId();

        assertNotNull(found);
        assertEquals(vehicle.getBrand(), found.getBrand());
        assertEquals(vehicle.getModel(), found.getModel());
        assertEquals(vehicle.getRequiredLicence(), found.getRequiredLicence());
        assertEquals(vehicle.getLastMaintenanceDate(), found.getLastMaintenanceDate());
        assertEquals(vehicle.getNextMaintenanceDate(), found.getNextMaintenanceDate());

        getVehicleDAO().delete(found);
        Vehicle deleted = getVehicleDAO().findById(id);
        assertNull(deleted);
    }

    @Test
    void testUpdateStatus() {
        vehicle.updateStatus(Vehicle.VehicleStatus.IN_MAINTENANCE);
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, vehicle.getVehicleStatus());
    }

    @Test
    void testLastMaintenanceDateDefault() {
        final int year = 2021;
        final int capacity = 2;
        Vehicle newVehicle = new Vehicle("CD456EF", "Mercedes", "Sprinter", year,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, capacity);
        assertEquals(LocalDate.now(), newVehicle.getLastMaintenanceDate());
    }

    @Test
    void testNextMaintenanceDateDefault() {
        final int year = 2021;
        final int capacity = 2;
        Vehicle newVehicle = new Vehicle("CD456EF", "Mercedes", "Sprinter", year,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, capacity);
        assertEquals(LocalDate.now().plusYears(1), newVehicle.getNextMaintenanceDate());
    }

    @Test
    void testToString() {
        String output = vehicle.toString();
        assertNotNull(output);
        assertTrue(output.contains("Vehicle"));
        assertTrue(output.contains(vehicle.getPlate()));
        assertTrue(output.contains(vehicle.getBrand()));
        assertTrue(output.contains(vehicle.getModel()));
    }
}
