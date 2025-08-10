package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Vehicle;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleManagerTest extends AbstractDatabaseTest {

    private static final int VEHICLE_REGISTRATION_YEAR = 2021;
    private Vehicle v1;
    private Vehicle v2;
    private Vehicle v3;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();

        v1 = new Vehicle("DD444DD", "Renault", "Master", VEHICLE_REGISTRATION_YEAR,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        v2 = new Vehicle("EE555EE", "MAN", "TGE", VEHICLE_REGISTRATION_YEAR,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_MAINTENANCE, 2);
        v3 = new Vehicle("FF666FF", "Ford", "Transit", VEHICLE_REGISTRATION_YEAR,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.OUT_OF_SERVICE, 3);

        getVehicleDAO().insert(v1);
        getVehicleDAO().insert(v2);
        getVehicleDAO().insert(v3);
    }

    @Test
    void testPlateNormalization() {
        Vehicle vehicleWithLowerCasePlate =
                new Vehicle("aa111aa", "Fiat", "Panda", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 1);
        vehicleWithLowerCasePlate.setLastMaintenanceDate(LocalDate.now());
        vehicleWithLowerCasePlate.setNextMaintenanceDate(LocalDate.now().plusYears(1));

        Vehicle savedVehicle = getVehicleManager().addVehicle(vehicleWithLowerCasePlate);

        assertEquals("AA111AA", savedVehicle.getPlate());

        Vehicle dbVehicle = getVehicleManager().findVehicleByPlate("aa111aa");
        assertNotNull(dbVehicle);
        assertEquals("AA111AA", dbVehicle.getPlate());
    }

    @Test
    void testUpdateVehiclePlate() {
        Vehicle vehicle =
                new Vehicle("CC333CC", "Toyota", "HiAce", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, 4);
        getVehicleManager().addVehicle(vehicle);

        vehicle.setPlate("cc333cc");
        getVehicleManager().updateVehicle(vehicle);

        Vehicle reloaded = getVehicleManager().findVehicleByPlate("CC333CC");
        assertNotNull(reloaded);
        assertEquals("CC333CC", reloaded.getPlate());
    }

    @Test
    void testAddVehicle() {
        Vehicle toAddVehicle =
                new Vehicle("gg000gg", "Renault", "Master", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        Vehicle saved = getVehicleManager().addVehicle(toAddVehicle);

        assertNotNull(saved);
        assertEquals("Renault", saved.getBrand());

        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().addVehicle(null));

        Vehicle duplicate =
                new Vehicle("gg000GG", "Renault", "Master", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().addVehicle(duplicate));

        Vehicle withSpaces =
                new Vehicle("  AA000BB ", "Brand", "Model", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 2);
        getVehicleManager().addVehicle(withSpaces);
        Vehicle savedNoSpaces = getVehicleManager().findVehicleByPlate("aa000bb");
        assertNotNull(savedNoSpaces);
        assertEquals("AA000BB", savedNoSpaces.getPlate());
    }

    @Test
    void testUpdateVehicle() {
        v1.setModel("Updated");
        getVehicleManager().updateVehicle(v1);

        Vehicle updated = getVehicleManager().findVehicleByPlate("DD444DD");
        assertNotNull(updated);
        assertEquals("Updated", updated.getModel());

        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().updateVehicle(null));

        Vehicle ghost =
                new Vehicle("ZZ999ZZ", "Ghost", "Model", VEHICLE_REGISTRATION_YEAR,
                        Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 1);
        assertDoesNotThrow(() -> getVehicleManager().updateVehicle(ghost));

        v1.setBrand(null);
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().updateVehicle(v1));
    }

    @Test
    void testMarkMaintenanceAsComplete() {
        assertEquals(Vehicle.VehicleStatus.IN_MAINTENANCE, v2.getVehicleStatus());

        getVehicleManager().markMaintenanceAsComplete(v2);
        Vehicle updated = getVehicleManager().findVehicleByPlate("EE555EE");

        assertNotNull(updated);
        assertEquals(Vehicle.VehicleStatus.IN_SERVICE, updated.getVehicleStatus());
        assertEquals(LocalDate.now(), updated.getLastMaintenanceDate());
        assertEquals(LocalDate.now().plusYears(1), updated.getNextMaintenanceDate());

        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().markMaintenanceAsComplete(null));
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().markMaintenanceAsComplete(v1));
    }
}
