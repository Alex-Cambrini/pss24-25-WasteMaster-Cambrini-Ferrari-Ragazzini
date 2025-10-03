package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Vehicle.RequiredLicence;
import it.unibo.wastemaster.domain.model.Vehicle.VehicleStatus;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for VehicleDAO.
 */
class VehicleDAOTest extends AbstractDatabaseTest {

    private Vehicle v1;
    private Vehicle v2;
    private Vehicle v3;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        final int year2020 = 2020;
        final int year2021 = 2021;
        final int year2022 = 2022;
        final int capacity3 = 3;
        final int capacity5 = 5;
        final int capacity4 = 4;

        v1 = new Vehicle("AA111AA", "Iveco", "Daily", year2020, RequiredLicence.C1,
                VehicleStatus.IN_SERVICE, capacity3);
        v2 = new Vehicle("BB222BB", "Mercedes", "Sprinter", year2021, RequiredLicence.C,
                VehicleStatus.IN_MAINTENANCE, capacity5);
        v3 = new Vehicle("CC333CC", "Fiat", "Ducato", year2022, RequiredLicence.C1,
                VehicleStatus.OUT_OF_SERVICE, capacity4);

        getVehicleDAO().insert(v1);
        getVehicleDAO().insert(v2);
        getVehicleDAO().insert(v3);
    }

    @Test
    void testFindByPlate() {
        final String plate1 = "AA111AA";
        final String plate2 = "BB222BB";

        Optional<Vehicle> found = getVehicleDAO().findByPlate(plate1);
        assertTrue(found.isPresent());
        assertEquals("Iveco", found.get().getBrand());
        assertEquals(RequiredLicence.C1, found.get().getRequiredLicence());

        Optional<Vehicle> found2 = getVehicleDAO().findByPlate(plate2);
        assertTrue(found2.isPresent());
        assertEquals("Mercedes", found2.get().getBrand());
        assertEquals(RequiredLicence.C, found2.get().getRequiredLicence());
    }

    @Test
    void testFindByPlateNotFound() {
        final String unknownPlate = "ZZ999ZZ";

        Optional<Vehicle> found = getVehicleDAO().findByPlate(unknownPlate);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByStatus() {
        List<Vehicle> inService = getVehicleDAO().findByStatus(VehicleStatus.IN_SERVICE);
        assertEquals(1, inService.size());
        assertEquals("AA111AA", inService.get(0).getPlate());
        assertEquals(VehicleStatus.IN_SERVICE, inService.get(0).getVehicleStatus());

        List<Vehicle> inMaintenance =
                getVehicleDAO().findByStatus(VehicleStatus.IN_MAINTENANCE);
        assertEquals(1, inMaintenance.size());
        assertEquals("BB222BB", inMaintenance.get(0).getPlate());
        assertEquals(VehicleStatus.IN_MAINTENANCE,
                inMaintenance.get(0).getVehicleStatus());

        List<Vehicle> outOfService =
                getVehicleDAO().findByStatus(VehicleStatus.OUT_OF_SERVICE);
        assertEquals(1, outOfService.size());
        assertEquals("CC333CC", outOfService.get(0).getPlate());
        assertEquals(VehicleStatus.OUT_OF_SERVICE,
                outOfService.get(0).getVehicleStatus());
    }

    @Test
    void testGetAllVehicles() {
        final String plate1 = "AA111AA";
        final String plate2 = "BB222BB";
        final String plate3 = "CC333CC";

        List<Vehicle> allVehicles = getVehicleDAO().getAllVehicles();
        assertEquals(3, allVehicles.size());
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals(plate1)));
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals(plate2)));
        assertTrue(allVehicles.stream().anyMatch(v -> v.getPlate().equals(plate3)));
    }

    @Test
    void testFindVehicleDetails() {
        final String plate1 = "AA111AA";
        final String plate2 = "BB222BB";
        final String plate3 = "CC333CC";

        List<Vehicle> result = getVehicleDAO().findVehicleDetails();

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(v -> v.getPlate().equals(plate1)));
        assertTrue(result.stream().anyMatch(v -> v.getPlate().equals(plate2)));
        assertTrue(result.stream().anyMatch(v -> v.getPlate().equals(plate3)));
    }
}
