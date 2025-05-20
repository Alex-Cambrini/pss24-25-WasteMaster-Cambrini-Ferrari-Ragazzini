package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripManagerTest extends AbstractDatabaseTest {

        private Vehicle vehicle;
        private Employee operator1, operator2;
        private LocalDateTime departureTime;
        private LocalDateTime expectedReturnTime;

        @BeforeEach
        public void setUp() {
                super.setUp();

                vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020,
                                Vehicle.RequiredLicence.C1, Vehicle.VehicleStatus.IN_SERVICE, 3);
                vehicleDAO.insert(vehicle);

                Employee emp1 = new Employee("John", "Doe",
                                new Location("Via Roma", "10", "Bologna", "40100"),
                                "john.doe@example.com", "+391234567890",
                                Employee.Role.OPERATOR, Employee.Licence.C1);
                Employee emp2 = new Employee("Anna", "Rossi",
                                new Location("Via Milano", "22", "Milano", "20100"),
                                "anna.rossi@example.com", "+390987654321",
                                Employee.Role.OPERATOR, Employee.Licence.C1);

                employeeDAO.insert(emp1);
                employeeDAO.insert(emp2);

                operator1 = emp1;
                operator2 = emp2;

                departureTime = LocalDateTime.now().plusHours(1);
                expectedReturnTime = departureTime.plusHours(5);
        }

        // @Test
        // void testCreateAndGetTrip() {

        // Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
        // Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);

        // Waste waste1 = new Waste("Organico", null, null);
        // Waste waste2 = new Waste("Carta", null, null);

        // OneTimeSchedule oneTime1 = new OneTimeSchedule( customer1, waste1,
        // LocalDate.now().plusDays(1));

        // RecurringSchedule recurring = new RecurringSchedule(customer2, waste2,
        // LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
        // recurring.setNextCollectionDate(LocalDate.now().plusDays(7));

        // Collection collection1 = new Collection(oneTime1);
        // Collection collection2 = new Collection(recurring);

        // List<Collection> collections = List.of(collection1, collection2);

        // tripManager.createTrip("40100", vehicle, List.of(operator1, operator2),
        // departureTime, expectedReturnTime, Trip.TripStatus.PENDING,collections);

        // List<Trip> allTrips = tripDAO.findAll();
        // assertFalse(allTrips.isEmpty());

        // Trip savedTrip = allTrips.get(0);
        // assertEquals("40100", savedTrip.getPostalCodes());
        // assertEquals(vehicle.getPlate(), savedTrip.getAssignedVehicle().getPlate());
        // assertEquals(2, savedTrip.getOperators().size());
        // assertEquals(departureTime, savedTrip.getDepartureTime());
        // assertEquals(expectedReturnTime, savedTrip.getExpectedReturnTime());
        // assertEquals(Trip.TripStatus.PENDING, savedTrip.getStatus());

        // Trip found = tripManager.getTripById(savedTrip.getTripId());
        // assertNotNull(found);
        // assertEquals(savedTrip.getTripId(), found.getTripId());
        // }

        // @Test
        // void testDeleteTrip() {

        // Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
        // Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);

        // Waste waste1 = new Waste("Organico", null, null);
        // Waste waste2 = new Waste("Carta", null, null);

        // OneTimeSchedule oneTime1 = new OneTimeSchedule( customer1, waste1,
        // LocalDate.now().plusDays(1));

        // RecurringSchedule recurring = new RecurringSchedule(customer2, waste2,
        // LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
        // recurring.setNextCollectionDate(LocalDate.now().plusDays(7));

        // Collection collection1 = new Collection(oneTime1);
        // Collection collection2 = new Collection(recurring);

        // List<Collection> collections = List.of(collection1, collection2);

        // tripManager.createTrip("40100", vehicle, List.of(operator1, operator2),
        // departureTime, expectedReturnTime, Trip.TripStatus.PENDING,collections);

        // Trip trip = tripDAO.findAll().get(0);
        // assertNotNull(trip);

        // tripManager.deleteTrip(trip.getTripId());

        // Trip deleted = tripDAO.findById(trip.getTripId());
        // assertNull(deleted);
        // }

        @Test
        void testTripPersistence() {
                Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
                Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);

                Waste waste1 = new Waste("Organico", null, null);
                Waste waste2 = new Waste("Carta", null, null);

                OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1, LocalDate.now().plusDays(1));

                RecurringSchedule recurring = new RecurringSchedule(customer2, waste2, LocalDate.now(),
                                RecurringSchedule.Frequency.WEEKLY);
                recurring.setNextCollectionDate(LocalDate.now().plusDays(7));

                Collection collection1 = new Collection(oneTime1);
                Collection collection2 = new Collection(recurring);

                List<Collection> collections = List.of(collection1, collection2);

                tripManager.createTrip("40100", vehicle, List.of(operator1, operator2),
                                departureTime, expectedReturnTime, Trip.TripStatus.PENDING, collections);

                // Ricarica dal DAO
                List<Trip> trips = tripDAO.findAll();
                assertEquals(1, trips.size());

                Trip trip = trips.get(0);
                assertEquals("40100", trip.getPostalCodes());
                assertEquals(operator1.getName(), trip.getOperators().get(0).getName());
                assertEquals(vehicle.getPlate(), trip.getAssignedVehicle().getPlate());
        }
}
