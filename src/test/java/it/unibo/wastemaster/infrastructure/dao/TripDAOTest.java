package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Employee.Role;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Trip.TripStatus;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Vehicle.RequiredLicence;
import it.unibo.wastemaster.domain.model.Vehicle.VehicleStatus;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripDAOTest extends AbstractDatabaseTest {

    private static final int MINUTES_30 = 30;
    private static final int MINUTES_15 = 15;
    private static final int DAYS_7 = 7;

    private LocalDateTime tripStart;
    private LocalDateTime tripEnd;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        tripStart =
                LocalDateTime.now().plusDays(3).withHour(8).withMinute(0).withSecond(0)
                        .withNano(0);
        tripEnd = tripStart.plusHours(4);
    }

    @Test
    void testFindAvailableVehicles() {
        Vehicle v1 = new Vehicle("AA111AA", "Iveco", "Daily",
                LocalDate.now().getYear(), RequiredLicence.C, VehicleStatus.IN_SERVICE,
                2);
        getVehicleDAO().insert(v1);

        Vehicle v2 = new Vehicle("BB222BB", "Iveco", "Daily",
                LocalDate.now().getYear(), RequiredLicence.C, VehicleStatus.IN_SERVICE,
                2);
        getVehicleDAO().insert(v2);

        Trip overlapping = new Trip(
                "40100", v2, Collections.emptyList(),
                tripStart.plusMinutes(MINUTES_30), tripEnd.minusMinutes(MINUTES_15),
                Collections.emptyList()
        );
        overlapping.setStatus(TripStatus.ACTIVE);
        getTripDAO().insert(overlapping);

        Vehicle v3 = new Vehicle("CC333CC", "Fiat", "Ducato",
                LocalDate.now().getYear(), RequiredLicence.C1,
                VehicleStatus.OUT_OF_SERVICE, 1);
        getVehicleDAO().insert(v3);

        Vehicle v4 = new Vehicle("DD444DD", "MAN", "TGL",
                LocalDate.now().getYear(), RequiredLicence.C, VehicleStatus.IN_SERVICE,
                2);
        v4.setNextMaintenanceDate(
                tripEnd.toLocalDate().minusDays(1)); // nextMaint < tripEnd → out
        getVehicleDAO().insert(v4);

        List<Vehicle> available = getTripDAO().findAvailableVehicles(tripStart, tripEnd);
        assertEquals(1, available.size());
        assertEquals("AA111AA", available.get(0).getPlate());
    }

    @Test
    void testFindAvailableOperators() {
        Location addr = new Location("Via Lavoro", "10", "Bologna", "40100");
        getLocationDAO().insert(addr);

        Employee e1 = new Employee("Mario", "Verdi", addr, "mario.verdi@example.com",
                "3331111111", Role.OPERATOR, Licence.C);
        Employee e2 = new Employee("Luigi", "Bianchi", addr, "luigi.bianchi@example.com",
                "3332222222", Role.OPERATOR, Licence.C);
        getEmployeeDAO().insert(e1);
        getEmployeeDAO().insert(e2);

        Vehicle v = new Vehicle("EE555EE", "Iveco", "Eurocargo",
                LocalDate.now().getYear(), RequiredLicence.C, VehicleStatus.IN_SERVICE,
                2);
        getVehicleDAO().insert(v);

        Trip t = new Trip(
                "40121", v, List.of(e2),
                tripStart.plusMinutes(10), tripEnd.minusMinutes(10),
                Collections.emptyList()
        );
        t.setStatus(TripStatus.ACTIVE);
        getTripDAO().insert(t);

        List<Employee> available =
                getTripDAO().findAvailableOperators(tripStart, tripEnd);

        assertTrue(available.stream()
                .anyMatch(op -> op.getEmployeeId().equals(e1.getEmployeeId())));
        assertFalse(available.stream()
                .anyMatch(op -> op.getEmployeeId().equals(e2.getEmployeeId())));
    }

    @Test
    void testFindAvailablePostalCodes() {
        Waste plastic = new Waste("PLASTIC", true, false);
        getWasteDAO().insert(plastic);

        Location loc1 = new Location("Via Roma", "1", "Bologna", "40100");
        Location loc2 = new Location("Via Milano", "2", "Bologna", "40121");
        getLocationDAO().insert(loc1);
        getLocationDAO().insert(loc2);

        Customer c1 = new Customer("Mario", "Rossi", loc1, "mario.rossi@example.com",
                "3330000001");
        Customer c2 = new Customer("Giulia", "Neri", loc2, "giulia.neri@example.com",
                "3330000002");
        getCustomerDAO().insert(c1);
        getCustomerDAO().insert(c2);

        LocalDate targetDate = LocalDate.now().plusDays(DAYS_7);

        OneTimeSchedule s1 = new OneTimeSchedule(c1, plastic, targetDate);
        s1.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s1);
        getCollectionDAO().insert(new Collection(s1));

        OneTimeSchedule s2 = new OneTimeSchedule(c2, plastic, targetDate);
        s2.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s2);
        getCollectionDAO().insert(new Collection(s2));

        OneTimeSchedule s3 = new OneTimeSchedule(c1, plastic, targetDate.plusDays(1));
        s3.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s3);
        getCollectionDAO().insert(new Collection(s3));

        OneTimeSchedule s4 = new OneTimeSchedule(c2, plastic, targetDate);
        s4.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s4);
        Collection col4 = new Collection(s4);
        col4.setCollectionStatus(CollectionStatus.CANCELLED);
        getCollectionDAO().insert(col4);

        List<String> postalCodes = getTripDAO().findAvailablePostalCodes(targetDate);

        assertEquals(2, postalCodes.size());
        assertTrue(postalCodes.contains("40100"));
        assertTrue(postalCodes.contains("40121"));
    }
}
