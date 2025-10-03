package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

        Optional<Vehicle> dbVehicleOpt =
                getVehicleManager().findVehicleByPlate("aa111aa");
        assertTrue(dbVehicleOpt.isPresent());
        Vehicle dbVehicle = dbVehicleOpt.get();
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

        Optional<Vehicle> reloadedOpt = getVehicleManager().findVehicleByPlate("CC333CC");
        assertTrue(reloadedOpt.isPresent());
        Vehicle reloaded = reloadedOpt.get();
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

        Optional<Vehicle> savedNoSpacesOpt =
                getVehicleManager().findVehicleByPlate("AA000BB");
        assertTrue(savedNoSpacesOpt.isPresent());
        Vehicle savedNoSpaces = savedNoSpacesOpt.get();
        assertEquals("AA000BB", savedNoSpaces.getPlate());
    }

    @Test
    void testUpdateVehicle() {
        v1.setModel("Updated");
        getVehicleManager().updateVehicle(v1);

        Optional<Vehicle> updatedOpt = getVehicleManager().findVehicleByPlate("DD444DD");
        assertTrue(updatedOpt.isPresent());
        Vehicle updated = updatedOpt.get();
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

        Optional<Vehicle> updatedOpt = getVehicleManager().findVehicleByPlate("EE555EE");
        assertTrue(updatedOpt.isPresent());
        Vehicle updated = updatedOpt.get();

        assertEquals(Vehicle.VehicleStatus.IN_SERVICE, updated.getVehicleStatus());
        assertEquals(LocalDate.now(), updated.getLastMaintenanceDate());
        assertEquals(LocalDate.now().plusYears(1), updated.getNextMaintenanceDate());

        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().markMaintenanceAsComplete(null));
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().markMaintenanceAsComplete(v1));
    }

    @Test
    void testDeleteVehicle() {
        Vehicle toDelete = new Vehicle("HH777HH", "Iveco", "Daily", 2022,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 2);
        getVehicleDAO().insert(toDelete);

        assertTrue(getVehicleManager().deleteVehicle(toDelete));
        assertTrue(!getVehicleManager().deleteVehicle(null));
        Vehicle noPlate = new Vehicle();
        assertTrue(!getVehicleManager().deleteVehicle(noPlate));
    }

    @Test
    void testGetAllowedLicences() {
        Vehicle vb = new Vehicle("AA100AA", "Brand", "Model", 2020,
                Vehicle.RequiredLicence.B, Vehicle.VehicleStatus.IN_SERVICE, 1);
        Vehicle vc1 = new Vehicle("AA101AA", "Brand", "Model", 2020,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 1);
        Vehicle vc = new Vehicle("AA102AA", "Brand", "Model", 2020,
                Vehicle.RequiredLicence.C, Vehicle.VehicleStatus.IN_SERVICE, 1);

        assertEquals(List.of(Licence.B, Licence.C1, Licence.C),
                getVehicleManager().getAllowedLicences(vb));
        assertEquals(List.of(Licence.C1, Licence.C),
                getVehicleManager().getAllowedLicences(vc1));
        assertEquals(List.of(Licence.C), getVehicleManager().getAllowedLicences(vc));
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().getAllowedLicences(null));
    }

    @Test
    void testFindAllVehicle() {
        var all = getVehicleManager().findAllVehicle();
        assertTrue(all.size() >= 3);
        assertTrue(all.stream().anyMatch(v -> "DD444DD".equals(v.getPlate())));
        assertTrue(all.stream().anyMatch(v -> "EE555EE".equals(v.getPlate())));
        assertTrue(all.stream().anyMatch(v -> "FF666FF".equals(v.getPlate())));
        assertThrows(IllegalArgumentException.class,
                () -> getVehicleManager().findVehicleByPlate(null));
    }
}
