package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripManagerTest extends AbstractDatabaseTest {

    private static final int VEHICLE_TEST_YEAR = 2020;
    private static final int TRIP_EXPECTED_DURATION_HOURS = 5;
    private static final int NEXT_COLLECTION_DAYS = 7;

    private Vehicle vehicle1;
    private Employee operator1, operator2;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        Location locBo = new Location("Via Roma", "10", "Bologna", "40100");
        Location locMi = new Location("Via Milano", "22", "Milano", "20100");
        getLocationDAO().insert(locBo);
        getLocationDAO().insert(locMi);

        vehicle1 = new Vehicle("AB123CD", "Iveco", "Daily", VEHICLE_TEST_YEAR,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        getVehicleDAO().insert(vehicle1);

        operator1 = new Employee("John", "Doe", locBo,
                "john.doe@example.com", "+391234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        operator2 = new Employee("Anna", "Rossi", locMi,
                "anna.rossi@example.com", "+390987654321",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        getEmployeeDAO().insert(operator1);
        getEmployeeDAO().insert(operator2);

        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(TRIP_EXPECTED_DURATION_HOURS);
    }

    private List<Collection> createCollections() {
        Location cLoc1 = new Location("Via Roma", "10", "Bologna", "40100");
        Location cLoc2 = new Location("Via Milano", "22", "Milano", "20100");
        getLocationDAO().insert(cLoc1);
        getLocationDAO().insert(cLoc2);

        Customer customer1 = new Customer("Mario", "Rossi", cLoc1,
                "mario.rossi@example.com", "1234567890");
        Customer customer2 = new Customer("Anna", "Bianchi", cLoc2,
                "anna.bianchi@example.com", "0987654321");
        getCustomerDAO().insert(customer1);
        getCustomerDAO().insert(customer2);

        Waste waste1 = new Waste("Organico", true, false);
        Waste waste2 = new Waste("Carta", true, false);
        getWasteDAO().insert(waste1);
        getWasteDAO().insert(waste2);
        DayOfWeek cartaDay =
                LocalDate.now().plusDays(NEXT_COLLECTION_DAYS).getDayOfWeek();
        WasteSchedule cartaSchedule = new WasteSchedule(waste2, cartaDay);
        getWasteScheduleDAO().insert(cartaSchedule);
        DayOfWeek organicoDay = LocalDate.now().getDayOfWeek();
        WasteSchedule organicoSchedule = new WasteSchedule(waste1, organicoDay);
        getWasteScheduleDAO().insert(organicoSchedule);

        OneTimeSchedule oneTime1 =
                new OneTimeSchedule(customer1, waste1, LocalDate.now().plusDays(1));
        RecurringSchedule recurring =
                new RecurringSchedule(customer2, waste2, LocalDate.now(),
                        RecurringSchedule.Frequency.WEEKLY);
        recurring.setNextCollectionDate(LocalDate.now().plusDays(NEXT_COLLECTION_DAYS));
        getOneTimeScheduleDAO().insert(oneTime1);
        getRecurringScheduleDAO().insert(recurring);

        Collection collection1 = new Collection(oneTime1);
        Collection collection2 = new Collection(recurring);

        return new ArrayList<>(List.of(collection1, collection2));
    }

    @Test
    void testCreateTrip() {
        List<Collection> collections = createCollections();

        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));

        List<Trip> trips = getTripDAO().findAll();
        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals("40100", trip.getPostalCode());
        assertEquals(2, trip.getOperators().size());
        assertEquals(vehicle1.getPlate(), trip.getAssignedVehicle().getPlate());
        assertEquals(Trip.TripStatus.ACTIVE, trip.getStatus());

        assertNotNull(trip.getCollections());
    }

    @Test
    void testGetTripById() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));

        Trip trip = getTripDAO().findAll().get(0);
        Optional<Trip> foundOpt = getTripManager().getTripById(trip.getTripId());
        assertTrue(foundOpt.isPresent());
        Trip found = foundOpt.get();
        assertEquals(trip.getTripId(), found.getTripId());
        assertEquals("40100", found.getPostalCode());

        Optional<Trip> none = getTripManager().getTripById(999_999);
        assertTrue(none.isEmpty());
    }

    @Test
    void testUpdateTrip_and_SoftDeleteTrip() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));

        Trip trip = getTripDAO().findAll().get(0);

        trip.setStatus(Trip.TripStatus.COMPLETED);
        getTripManager().updateTrip(trip);

        Trip afterUpdate = getTripDAO().findById(trip.getTripId()).orElseThrow();
        assertEquals(Trip.TripStatus.COMPLETED, afterUpdate.getStatus());

        assertFalse(getTripManager().softDeleteTrip(afterUpdate));

        List<Collection> collections2 = createCollections();
        getTripManager().createTrip(
                "20100",
                vehicle1,
                new ArrayList<>(List.of(operator1)),
                departureTime.plusDays(1),
                expectedReturnTime.plusDays(1),
                new ArrayList<>(collections2));
        Trip activeTrip = getTripDAO().findAll().stream()
                .filter(t -> t.getStatus() == Trip.TripStatus.ACTIVE)
                .findFirst().orElseThrow();
        assertTrue(getTripManager().softDeleteTrip(activeTrip));
        Trip canceled =
                getTripManager().getTripById(activeTrip.getTripId()).orElseThrow();
        assertEquals(Trip.TripStatus.CANCELED, canceled.getStatus());

        assertFalse(getTripManager().softDeleteTrip(null));
        Trip noId = new Trip();
        noId.setCollections(new ArrayList<>());
        assertFalse(getTripManager().softDeleteTrip(noId));
    }

    @Test
    void testUpdateVehicle_and_UpdateOperators() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));
        Trip trip = getTripDAO().findAll().get(0);

        Vehicle vehicle2 = new Vehicle("ZZ999YY", "Fiat", "Ducato", 2021,
                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
        getVehicleDAO().insert(vehicle2);

        getTripManager().updateVehicle(trip.getTripId(), vehicle2);
        getTripManager().updateOperators(trip.getTripId(),
                new ArrayList<>(List.of(operator2)));

        Trip updated = getTripManager().getTripById(trip.getTripId()).orElseThrow();
        assertEquals("40100", updated.getPostalCode());
        assertEquals(vehicle2.getPlate(), updated.getAssignedVehicle().getPlate());
        assertEquals(1, updated.getOperators().size());
        assertEquals(operator2.getEmail(), updated.getOperators().get(0).getEmail());

        assertThrows(IllegalArgumentException.class,
                () -> getTripManager().updateVehicle(999_999, vehicle2));
        assertThrows(IllegalArgumentException.class,
                () -> getTripManager().updateVehicle(trip.getTripId(), null));
        assertThrows(IllegalArgumentException.class,
                () -> getTripManager().updateOperators(999_999,
                        new ArrayList<>(List.of(operator1))));
        assertThrows(IllegalArgumentException.class,
                () -> getTripManager().updateOperators(trip.getTripId(), null));
        assertThrows(IllegalArgumentException.class,
                () -> getTripManager().updateOperators(trip.getTripId(),
                        new ArrayList<>()));
    }

    @Test
    void testGetTripByIdNotFound() {
        Optional<Trip> none = getTripManager().getTripById(999_999);
        assertTrue(none.isEmpty());
    }

    @Test
    void testGetAvailableVehicles() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100", vehicle1, new ArrayList<>(List.of(operator1)),
                departureTime, expectedReturnTime, new ArrayList<>(collections));

        List<Vehicle> available =
                getTripManager().getAvailableVehicles(departureTime, expectedReturnTime);
        assertTrue(available.stream()
                .noneMatch(v -> v.getPlate().equals(vehicle1.getPlate())));
    }

    @Test
    void testGetAvailableOperatorsExcludeDriver() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100", vehicle1, new ArrayList<>(List.of(operator2)),
                departureTime, expectedReturnTime, new ArrayList<>(collections));

        List<Employee> ops = getTripManager().getAvailableOperatorsExcludeDriver(
                departureTime, expectedReturnTime, operator1);

        assertTrue(
                ops.stream().noneMatch(e -> e.getEmail().equals(operator1.getEmail())));
        assertTrue(
                ops.stream().noneMatch(e -> e.getEmail().equals(operator2.getEmail())));
    }

    @Test
    void testGetQualifiedDrivers() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100", vehicle1, new ArrayList<>(List.of(operator2)),
                departureTime, expectedReturnTime, new ArrayList<>(collections));

        List<Licence> allowed = List.of(Employee.Licence.C1);
        List<Employee> drivers = getTripManager().getQualifiedDrivers(
                departureTime, expectedReturnTime, allowed);

        assertTrue(drivers.stream()
                .anyMatch(e -> e.getEmail().equals(operator1.getEmail())));
        assertTrue(drivers.stream()
                .noneMatch(e -> e.getEmail().equals(operator2.getEmail())));
    }

    @Test
    void testGetAvailablePostalCodes() {
        LocalDate day = LocalDate.now().plusDays(1);

        Location cLoc = new Location("Via Roma", "10", "Bologna", "40100");
        getLocationDAO().insert(cLoc);

        Customer cust = new Customer("Mario", "Rossi", cLoc, "mario.rossi@example.com",
                "1234567890");
        getCustomerDAO().insert(cust);

        Waste waste = new Waste("Organico", true, false);
        getWasteDAO().insert(waste);

        OneTimeSchedule oneTime = new OneTimeSchedule(cust, waste, day);
        getOneTimeScheduleDAO().insert(oneTime);

        Collection c = new Collection(oneTime);
        getCollectionDAO().insert(c);
        List<String> caps = getTripManager().getAvailablePostalCodes(day);

        assertTrue(caps.contains("40100"));
    }

    @Test
    void testGetTripsForCurrentUser() {
        List<Collection> collections1 = createCollections();
        List<Collection> collections2 = createCollections();

        getTripManager().createTrip(
                "40100", vehicle1,
                new ArrayList<>(List.of(operator1)),
                departureTime, expectedReturnTime,
                new ArrayList<>(collections1));

        getTripManager().createTrip(
                "20100", vehicle1,
                new ArrayList<>(List.of(operator2)),
                departureTime.plusDays(1), expectedReturnTime.plusDays(1),
                new ArrayList<>(collections2));

        Employee admin = new Employee("Admin", "User",
                new Location("Via Admin", "1", "Bologna", "40100"),
                "admin@example.com", "0000000000", Employee.Role.ADMINISTRATOR,
                Employee.Licence.C1);
        List<Trip> allTrips = getTripManager().getTripsForCurrentUser(admin);
        assertTrue(allTrips.size() >= 2);

        List<Trip> operator1Trips = getTripManager().getTripsForCurrentUser(operator1);
        assertTrue(operator1Trips.stream()
                .allMatch(t -> t.getOperators().contains(operator1)));
        List<Trip> operator2Trips = getTripManager().getTripsForCurrentUser(operator2);
        assertTrue(operator2Trips.stream()
                .allMatch(t -> t.getOperators().contains(operator2)));
    }

    @Test
    void testSetTripAsCompleted_allCases() {
        // Create an ACTIVE trip with ACTIVE collections
        List<Collection> collections = createCollections();
        collections.forEach(
                c -> c.setCollectionStatus(Collection.CollectionStatus.ACTIVE));
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));
        Trip trip = getTripDAO().findAll().get(0);

        // Case 1: ACTIVE trip -> should become COMPLETED and all collections should be
        // COMPLETED
        assertEquals(Trip.TripStatus.ACTIVE, trip.getStatus());
        boolean completed = getTripManager().setTripAsCompleted(trip);
        assertTrue(completed);
        Trip updated = getTripManager().getTripById(trip.getTripId()).orElseThrow();
        assertEquals(Trip.TripStatus.COMPLETED, updated.getStatus());
        updated.getCollections().forEach(
                c -> assertEquals(Collection.CollectionStatus.COMPLETED,
                        c.getCollectionStatus()));

        // Case 2: already COMPLETED trip -> should not change
        boolean completedAgain = getTripManager().setTripAsCompleted(updated);
        assertFalse(completedAgain);

        // Case 3: CANCELED trip -> should not change
        updated.setStatus(Trip.TripStatus.CANCELED);
        getTripManager().updateTrip(updated);
        boolean completedCanceled = getTripManager().setTripAsCompleted(updated);
        assertFalse(completedCanceled);

        // Case 4: trip with NON ACTIVE collections -> should not change
        updated.setStatus(Trip.TripStatus.ACTIVE);
        getTripManager().updateTrip(updated);
        updated.getCollections().forEach(
                c -> c.setCollectionStatus(Collection.CollectionStatus.COMPLETED));
        updated.getCollections().forEach(c -> getCollectionDAO().update(c));
        boolean completedWithNonActiveCollections =
                getTripManager().setTripAsCompleted(updated);
        assertFalse(completedWithNonActiveCollections);

        // Case 5: null trip -> should not change
        boolean completedNull = getTripManager().setTripAsCompleted(null);
        assertFalse(completedNull);
    }

    @Test
    void testGetCollectionsByTrip() {
        List<Collection> collections = createCollections();
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));
        Trip trip = getTripDAO().findAll().get(0);

        List<Collection> result = getTripManager().getCollectionsByTrip(trip);
        assertEquals(collections.size(), result.size());
        assertTrue(result.containsAll(collections));

        getTripManager().createTrip(
                "20100",
                vehicle1,
                new ArrayList<>(List.of(operator1)),
                departureTime.plusDays(1),
                expectedReturnTime.plusDays(1),
                new ArrayList<>());
        Trip emptyTrip = getTripDAO().findAll().stream()
                .filter(t -> t.getPostalCode().equals("20100"))
                .findFirst().orElseThrow();
        List<Collection> emptyResult = getTripManager().getCollectionsByTrip(emptyTrip);
        assertTrue(emptyResult.isEmpty());

        assertThrows(NullPointerException.class,
                () -> getTripManager().getCollectionsByTrip(null));
    }

    @Test
    void testSoftDeleteAndRescheduleNextCollection() {
        List<Collection> collections = createCollections();

        collections.forEach(
                c -> c.setCollectionStatus(Collection.CollectionStatus.ACTIVE));
        getTripManager().createTrip(
                "40100",
                vehicle1,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime,
                expectedReturnTime,
                new ArrayList<>(collections));
        Trip trip = getTripDAO().findAll().get(0);
        assertEquals(Trip.TripStatus.ACTIVE, trip.getStatus());

        RecurringSchedule recurring = getRecurringScheduleDAO().findAll().stream()
                .findFirst().orElseThrow();
        LocalDate oldNext = recurring.getNextCollectionDate();
        DayOfWeek allowedDay = oldNext.getDayOfWeek();
        WasteSchedule cartaSchedule = new WasteSchedule(recurring.getWaste(), allowedDay);
        getWasteScheduleDAO().insert(cartaSchedule);

        boolean ok = getTripManager().softDeleteAndRescheduleNextCollection(trip);
        assertTrue(ok, "Soft delete + reschedule should succeed");

        Trip updatedTrip = getTripManager().getTripById(trip.getTripId()).orElseThrow();
        assertEquals(Trip.TripStatus.CANCELED, updatedTrip.getStatus());
        updatedTrip.getCollections().forEach(
                c -> assertEquals(Collection.CollectionStatus.CANCELLED,
                        c.getCollectionStatus()));

        RecurringSchedule after =
                getRecurringScheduleDAO().findById(recurring.getScheduleId())
                        .orElseThrow();
        assertEquals(oldNext.plusDays(NEXT_COLLECTION_DAYS),
                after.getNextCollectionDate(),
                "NextCollectionDate should be moved forward by the frequency");
    }
}
