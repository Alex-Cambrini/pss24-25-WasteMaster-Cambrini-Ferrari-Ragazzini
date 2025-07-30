package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.*;


import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripManagerTest extends AbstractDatabaseTest {

    private Vehicle vehicle1;
    private Employee operator1, operator2;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();

        

        vehicle1 = new Vehicle("AB123CD", "Iveco", "Daily", 2020,
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
        expectedReturnTime = departureTime.plusHours(5);
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

        OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1, LocalDate.now().plusDays(1));
        RecurringSchedule recurring = new RecurringSchedule(customer2, waste2, LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
        recurring.setNextCollectionDate(LocalDate.now().plusDays(7));

        getOneTimeScheduleDAO().insert(oneTime1);
        getRecurringScheduleDAO().insert(recurring);

        Collection collection1 = new Collection(oneTime1);
        Collection collection2 = new Collection(recurring);

        getCollectionDAO().insert(collection1);
        getCollectionDAO().insert(collection2);

        return List.of(collection1, collection2);
    }

    @Test
    void testCreateTrip() {
        List<Collection> collections = createCollections();

        getTripManager().createTrip("40100", vehicle1,
                List.of(operator1, operator2), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING, collections);

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

        getTripManager().createTrip("40100", vehicle1,
                List.of(operator1, operator2), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING, collections);

        Trip trip = getTripDAO().findAll().get(0);

        Trip found = getTripManager().getTripById(trip.getTripId());
        assertNotNull(found);
        assertEquals(trip.getTripId(), found.getTripId());
        assertEquals("40100", found.getPostalCodes());
    }

    @Test
        void testUpdateTrip() {
        List<Collection> collections = createCollections();

        getTripManager().createTrip("40100", vehicle1,
                List.of(operator1, operator2), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING, collections);

        Trip trip = getTripDAO().findAll().get(0);

        
        getTripManager().updateTrip(trip.getTripId(), "20100", vehicle1,
                new ArrayList<>(List.of(operator2)), 
                departureTime.plusDays(1),
                expectedReturnTime.plusDays(1), Trip.TripStatus.COMPLETED,
                new ArrayList<>(collections)); 

        Trip updated = getTripManager().getTripById(trip.getTripId());
        assertEquals("20100", updated.getPostalCodes());
        assertEquals(vehicle1.getPlate(), updated.getAssignedVehicle().getPlate());
        assertEquals(1, updated.getOperators().size());
        assertEquals(Trip.TripStatus.COMPLETED, updated.getStatus());
        }

    @Test
    void testDeleteTrip() {
        List<Collection> collections = createCollections();

        getTripManager().createTrip("40100", vehicle1,
                List.of(operator1, operator2), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING, collections);

        Trip trip = getTripDAO().findAll().get(0);
        assertNotNull(trip);

        getTripManager().deleteTrip(trip.getTripId());

        Trip deleted = getTripDAO().findById(trip.getTripId());
        assertNull(deleted);
    }
}