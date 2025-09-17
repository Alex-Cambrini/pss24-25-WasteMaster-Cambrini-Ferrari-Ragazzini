package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Employee;
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
                vehicle1 = new Vehicle("AB123CD", "Iveco", "Daily", VEHICLE_TEST_YEAR,
                                Vehicle.RequiredLicence.C1,
                                Vehicle.VehicleStatus.IN_SERVICE, 3);

                getVehicleDAO().insert(vehicle1);

                operator1 = new Employee("John", "Doe",
                                new Location("Via Roma", "10", "Bologna", "40100"),
                                "john.doe@example.com", "+391234567890",
                                Employee.Role.OPERATOR, Employee.Licence.C1);
                operator2 = new Employee("Anna", "Rossi",
                                new Location("Via Milano", "22", "Milano", "20100"),
                                "anna.rossi@example.com", "+390987654321",
                                Employee.Role.OPERATOR, Employee.Licence.C1);

                getEmployeeDAO().insert(operator1);
                getEmployeeDAO().insert(operator2);

                departureTime = LocalDateTime.now().plusHours(1);
                expectedReturnTime = departureTime.plusHours(TRIP_EXPECTED_DURATION_HOURS);
        }

        private List<Collection> createCollections() {
                Customer customer1 = new Customer("Mario", "Rossi",
                                new Location("Via Roma", "10", "Bologna", "40100"),
                                "mario.rossi@example.com", "1234567890");
                Customer customer2 = new Customer("Anna", "Bianchi",
                                new Location("Via Milano", "22", "Milano", "20100"),
                                "anna.bianchi@example.com", "0987654321");

                getCustomerDAO().insert(customer1);
                getCustomerDAO().insert(customer2);

                Waste waste1 = new Waste("Organico", true, false);
                Waste waste2 = new Waste("Carta", true, false);

                getWasteDAO().insert(waste1);
                getWasteDAO().insert(waste2);

                OneTimeSchedule oneTime1 = new OneTimeSchedule(
                                customer1,
                                waste1,
                                LocalDate.now().plusDays(1));
                RecurringSchedule recurring = new RecurringSchedule(
                                customer2,
                                waste2,
                                LocalDate.now(),
                                RecurringSchedule.Frequency.WEEKLY);
                recurring.setNextCollectionDate(LocalDate.now().plusDays(NEXT_COLLECTION_DAYS));

                getOneTimeScheduleDAO().insert(oneTime1);
                getRecurringScheduleDAO().insert(recurring);

                Collection collection1 = new Collection(oneTime1);
                Collection collection2 = new Collection(recurring);

                getCollectionDAO().insert(collection1);
                getCollectionDAO().insert(collection2);
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
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                List<Trip> trips = getTripDAO().findAll();
                assertEquals(1, trips.size());

                Trip trip = trips.get(0);
                assertEquals("40100", trip.getPostalCodes());
                assertEquals(2, trip.getOperators().size());
                assertEquals(vehicle1.getPlate(), trip.getAssignedVehicle().getPlate());
                assertEquals(Trip.TripStatus.PENDING, trip.getStatus());
                assertEquals(2, trip.getCollections().size());
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
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);
                Optional<Trip> foundOpt = getTripManager().getTripById(trip.getTripId());
                assertTrue(foundOpt.isPresent());
                Trip found = foundOpt.get();
                assertEquals(trip.getTripId(), found.getTripId());
                assertEquals("40100", found.getPostalCodes());
                Optional<Trip> none = getTripManager().getTripById(999_999);
                assertTrue(none.isEmpty());
        }

        @Test
        void testUpdateTrip() {
                List<Collection> collections = createCollections();
                getTripManager().createTrip(
                                "40100",
                                vehicle1,
                                new ArrayList<>(List.of(operator1, operator2)),
                                departureTime,
                                expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);

                trip.setPostalCodes("20100");
                trip.setAssignedVehicle(vehicle1);
                trip.setOperators(new ArrayList<>(List.of(operator2)));
                trip.setDepartureTime(departureTime.plusDays(1));
                trip.setExpectedReturnTime(expectedReturnTime.plusDays(1));
                trip.setStatus(Trip.TripStatus.COMPLETED);
                trip.setCollections(new ArrayList<>(collections));

                getTripManager().updateTrip(trip);

                Optional<Trip> updatedOpt = getTripManager().getTripById(trip.getTripId());
                assertTrue(updatedOpt.isPresent());

                Trip updated = updatedOpt.get();
                assertEquals("20100", updated.getPostalCodes());
                assertEquals(vehicle1.getPlate(), updated.getAssignedVehicle().getPlate());
                assertEquals(1, updated.getOperators().size());
                assertEquals(Trip.TripStatus.COMPLETED, updated.getStatus());
                assertEquals(departureTime.plusDays(1), updated.getDepartureTime());
                assertEquals(expectedReturnTime.plusDays(1), updated.getExpectedReturnTime());
                assertEquals(collections.size(), updated.getCollections().size());
                Trip t = new Trip("40100", vehicle1,
                                new ArrayList<>(List.of(operator1)),
                                departureTime, expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(createCollections()));
                assertThrows(IllegalArgumentException.class, () -> getTripManager().updateTrip(t));
        }

        @Test
        void testDeleteTrip() {
                List<Collection> collections = createCollections();
                getTripManager().createTrip(
                                "40100",
                                vehicle1,
                                new ArrayList<>(List.of(operator1, operator2)),
                                departureTime,
                                expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);
                assertNotNull(trip);

                boolean firstDeleted = getTripManager().deleteTrip(trip.getTripId());
                assertTrue(firstDeleted);

                Optional<Trip> deletedOpt = getTripDAO().findById(trip.getTripId());
                assertTrue(deletedOpt.isEmpty());

                boolean secondDeleted = getTripManager().deleteTrip(trip.getTripId());
                assertFalse(secondDeleted);
        }

        @Test
        void testUpdateOperators() {
                List<Collection> collections = createCollections();
                getTripManager().createTrip(
                                "40100",
                                vehicle1,
                                new ArrayList<>(List.of(operator1)),
                                departureTime,
                                expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);
                getTripManager().updateOperators(trip.getTripId(), new ArrayList<>(List.of(operator2)));

                Trip updated = getTripDAO().findById(trip.getTripId()).get();
                assertEquals(1, updated.getOperators().size());
                assertEquals(operator2.getEmail(), updated.getOperators().get(0).getEmail());

                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateOperators(999_999, new ArrayList<>(List.of(operator1))));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateOperators(trip.getTripId(), null));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateOperators(trip.getTripId(), new ArrayList<>()));
        }

        @Test
        void testUpdateVehicle() {
                List<Collection> collections = createCollections();
                getTripManager().createTrip(
                                "40100", vehicle1,
                                new ArrayList<>(List.of(operator1)),
                                departureTime, expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);
                Vehicle vehicle2 = new Vehicle("ZZ999YY", "Fiat", "Ducato", 2021,
                                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
                getVehicleDAO().insert(vehicle2);
                getTripManager().updateVehicle(trip.getTripId(), vehicle2);

                Trip updated = getTripDAO().findById(trip.getTripId()).get();
                assertEquals(vehicle2.getPlate(), updated.getAssignedVehicle().getPlate());
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateVehicle(999_999, vehicle2));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().updateVehicle(trip.getTripId(), null));
        }

        @Test
        void testCancelTrip() {
                List<Collection> collections = createCollections();

                getTripManager().createTrip(
                                "40100", vehicle1,
                                new ArrayList<>(List.of(operator1, operator2)),
                                departureTime, expectedReturnTime,
                                Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                Trip trip = getTripDAO().findAll().get(0);

                getTripManager().cancelTrip(trip.getTripId());

                Trip canceled = getTripDAO().findById(trip.getTripId()).get();
                assertEquals(Trip.TripStatus.CANCELED, canceled.getStatus());
                assertTrue(canceled.getCollections().stream()
                                .allMatch(c -> c.getCollectionStatus() == Collection.CollectionStatus.FAILED));
                assertThrows(IllegalArgumentException.class,
                                () -> getTripManager().cancelTrip(999_999));
        }

        @Test
        void testFindAllTrips() {
                List<Collection> collections = createCollections();

                getTripManager().createTrip(
                                "40100", vehicle1,
                                new ArrayList<>(List.of(operator1)),
                                departureTime, expectedReturnTime, Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));
                getTripManager().createTrip(
                                "20100", vehicle1,
                                new ArrayList<>(List.of(operator2)),
                                departureTime.plusDays(1), expectedReturnTime.plusDays(1), Trip.TripStatus.PENDING,
                                new ArrayList<>(collections));

                List<Trip> all = getTripManager().findAllTrips();
                assertTrue(all.size() >= 2);
        }

        @Test
        void testGetTripByIdNotFound() {
                Optional<Trip> none = getTripManager().getTripById(999_999);
                assertTrue(none.isEmpty());
        }

}
