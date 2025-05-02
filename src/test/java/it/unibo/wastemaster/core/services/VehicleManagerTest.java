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
                v2 = new Vehicle("EE555EE", "MAN", "TGE", 2022, Vehicle.LicenceType.C,
                                Vehicle.VehicleStatus.IN_MAINTENANCE);
                v3 = new Vehicle("FF666FF", "Ford", "Transit", 2021, Vehicle.LicenceType.C1,
                                Vehicle.VehicleStatus.OUT_OF_SERVICE);

                vehicleDAO.insert(v1);
                vehicleDAO.insert(v2);
                vehicleDAO.insert(v3);
        }

        @Test
        public void testPlateNormalization() {
                Vehicle vehicleWithLowerCasePlate = new Vehicle("aa111aa", "Fiat", "Panda", 2024,
                                Vehicle.LicenceType.C1,
                                Vehicle.VehicleStatus.IN_SERVICE);
                vehicleWithLowerCasePlate.setLastMaintenanceDate(LocalDate.now());
                vehicleWithLowerCasePlate.setNextMaintenanceDate(LocalDate.now().plusYears(1));

                Vehicle savedVehicle = vehicleManager.addVehicle(vehicleWithLowerCasePlate);

                assertEquals("AA111AA", savedVehicle.getPlate());

                Vehicle dbVehicle = vehicleManager.findVehicleByPlate("aa111aa");
                assertNotNull(dbVehicle);
                assertEquals("AA111AA", dbVehicle.getPlate());
        }

        @Test
        public void testUpdateVehiclePlate() {
                Vehicle vehicle = new Vehicle("CC333CC", "Toyota", "HiAce", 2026,
                                Vehicle.LicenceType.C,
                                Vehicle.VehicleStatus.IN_SERVICE);
                vehicleManager.addVehicle(vehicle);

                vehicle.setPlate("cc333cc");
                vehicleManager.updateVehicle(vehicle);

                Vehicle reloaded = vehicleManager.findVehicleByPlate("CC333CC");
                assertNotNull(reloaded);
                System.out.println(vehicle.getPlate());
                assertEquals("CC333CC", reloaded.getPlate());
        }

        @Test
        public void testAddVehicle() {

                Vehicle toAddVeichle = new Vehicle("gg000gg", "Renault", "Master", 2023, Vehicle.LicenceType.C1,
                                Vehicle.VehicleStatus.IN_SERVICE);
                Vehicle saved = vehicleManager.addVehicle(toAddVeichle);

                assertNotNull(saved);
                assertEquals("Renault", saved.getBrand());

                assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(null));

                Vehicle duplicate = new Vehicle("gg000GG", "Renault", "Master", 2023, Vehicle.LicenceType.C1,
                                Vehicle.VehicleStatus.IN_SERVICE);


                assertThrows(IllegalArgumentException.class, () -> vehicleManager.addVehicle(duplicate));

                Vehicle withSpaces = new Vehicle("  AA000BB ", "Brand", "Model", 2024, Vehicle.LicenceType.C1,
                                Vehicle.VehicleStatus.IN_SERVICE);

                vehicleManager.addVehicle(withSpaces);
                Vehicle savedNoSpaces = vehicleManager.findVehicleByPlate("aa000bb");
                assertNotNull(savedNoSpaces);
                assertEquals("AA000BB", savedNoSpaces.getPlate());

        }

        @Test
        public void testUpdateVehicle() {
                v1.setModel("Updated");
                vehicleManager.updateVehicle(v1);

                Vehicle updated = vehicleManager.findVehicleByPlate("DD444DD");
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
                Vehicle updated = vehicleManager.findVehicleByPlate("EE555EE");

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
