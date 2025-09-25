package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
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

                OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1, LocalDate.now().plusDays(1));
                RecurringSchedule recurring = new RecurringSchedule(customer2, waste2, LocalDate.now(),
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
        Trip canceled = getTripManager().getTripById(activeTrip.getTripId()).orElseThrow();
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
                getTripManager().updateOperators(trip.getTripId(), new ArrayList<>(List.of(operator2)));

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
                                () -> getTripManager().updateOperators(999_999, new ArrayList<>(List.of(operator1))));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateOperators(trip.getTripId(), null));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateOperators(trip.getTripId(), new ArrayList<>()));
        }

        @Test
        void testFindAllTrips() {
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

                List<Trip> all = getTripManager().findAllTrips();
                assertTrue(all.size() >= 2);
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

                List<Vehicle> available = getTripManager().getAvailableVehicles(departureTime, expectedReturnTime);
                assertTrue(available.stream().noneMatch(v -> v.getPlate().equals(vehicle1.getPlate())));
        }

        @Test
        void testGetAvailableOperatorsExcludeDriver() {
                List<Collection> collections = createCollections();
                getTripManager().createTrip(
                                "40100", vehicle1, new ArrayList<>(List.of(operator2)),
                                departureTime, expectedReturnTime, new ArrayList<>(collections));

                List<Employee> ops = getTripManager().getAvailableOperatorsExcludeDriver(
                                departureTime, expectedReturnTime, operator1);

                assertTrue(ops.stream().noneMatch(e -> e.getEmail().equals(operator1.getEmail())));
                assertTrue(ops.stream().noneMatch(e -> e.getEmail().equals(operator2.getEmail())));
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

                assertTrue(drivers.stream().anyMatch(e -> e.getEmail().equals(operator1.getEmail())));
                assertTrue(drivers.stream().noneMatch(e -> e.getEmail().equals(operator2.getEmail())));
        }

        @Test
        void testGetAvailablePostalCodes() {
                LocalDate day = LocalDate.now().plusDays(1);

                Location cLoc = new Location("Via Roma", "10", "Bologna", "40100");
                getLocationDAO().insert(cLoc);

                Customer cust = new Customer("Mario", "Rossi", cLoc, "mario.rossi@example.com", "1234567890");
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

}
