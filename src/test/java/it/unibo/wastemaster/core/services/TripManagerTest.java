package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Waste;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TripManagerTest extends AbstractDatabaseTest {

    private Vehicle vehicle;
    private Employee operator1, operator2;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        final int vehicleYear = 2020;
        final int vehicleCapacity = 3;

        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", vehicleYear,
                Vehicle.RequiredLicence.C1,
                Vehicle.VehicleStatus.IN_SERVICE, vehicleCapacity);
        getVehicleDAO().insert(vehicle);

        Employee emp1 = new Employee("John", "Doe",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "john.doe@example.com", "+391234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        Employee emp2 = new Employee("Anna", "Rossi",
                new Location("Via Milano", "22", "Milano", "20100"),
                "anna.rossi@example.com", "+390987654321",
                Employee.Role.OPERATOR, Employee.Licence.C1);

        getEmployeeDAO().insert(emp1);
        getEmployeeDAO().insert(emp2);

        operator1 = emp1;
        operator2 = emp2;

        departureTime = LocalDateTime.now().plusHours(1);
        final int hoursUntilReturn = 5;
        expectedReturnTime = departureTime.plusHours(hoursUntilReturn);
    }

    // @Test
    // void testCreateAndGetTrip() {
    //
    // Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
    // Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);
    //
    // Waste waste1 = new Waste("Organico", null, null);
    // Waste waste2 = new Waste("Carta", null, null);
    //
    // OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1,
    // LocalDate.now().plusDays(1));
    //
    // RecurringSchedule recurring = new RecurringSchedule(customer2, waste2,
    // LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
    // recurring.setNextCollectionDate(LocalDate.now().plusDays(7));
    //
    // Collection collection1 = new Collection(oneTime1);
    // Collection collection2 = new Collection(recurring);
    //
    // List<Collection> collections = List.of(collection1, collection2);
    //
    // getTripManager().createTrip("40100", vehicle, List.of(operator1, operator2),
    // departureTime, expectedReturnTime, Trip.TripStatus.PENDING, collections);
    //
    // List<Trip> allTrips = getTripDAO().findAll();
    // assertFalse(allTrips.isEmpty());
    //
    // Trip savedTrip = allTrips.get(0);
    // assertEquals("40100", savedTrip.getPostalCodes());
    // assertEquals(vehicle.getPlate(), savedTrip.getAssignedVehicle().getPlate());
    // assertEquals(2, savedTrip.getOperators().size());
    // assertEquals(departureTime, savedTrip.getDepartureTime());
    // assertEquals(expectedReturnTime, savedTrip.getExpectedReturnTime());
    // assertEquals(Trip.TripStatus.PENDING, savedTrip.getStatus());
    //
    // Trip found = getTripManager().getTripById(savedTrip.getTripId());
    // assertNotNull(found);
    // assertEquals(savedTrip.getTripId(), found.getTripId());
    // }

    // @Test
    // void testDeleteTrip() {
    //
    // Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
    // Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);
    //
    // Waste waste1 = new Waste("Organico", null, null);
    // Waste waste2 = new Waste("Carta", null, null);
    //
    // OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1,
    // LocalDate.now().plusDays(1));
    //
    // RecurringSchedule recurring = new RecurringSchedule(customer2, waste2,
    // LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
    // recurring.setNextCollectionDate(LocalDate.now().plusDays(7));
    //
    // Collection collection1 = new Collection(oneTime1);
    // Collection collection2 = new Collection(recurring);
    //
    // List<Collection> collections = List.of(collection1, collection2);
    //
    // getTripManager().createTrip("40100", vehicle, List.of(operator1, operator2),
    // departureTime, expectedReturnTime, Trip.TripStatus.PENDING, collections);
    //
    // Trip trip = getTripDAO().findAll().get(0);
    // assertNotNull(trip);
    //
    // getTripManager().deleteTrip(trip.getTripId());
    //
    // Trip deleted = getTripDAO().findById(trip.getTripId());
    // assertNull(deleted);
    // }

    @Test
    void testTripPersistence() {
        final int daysUntilNextCollection = 7;

        Customer customer1 = new Customer("Mario Rossi", null, null, null, null);
        Customer customer2 = new Customer("Anna Bianchi", null, null, null, null);

        Waste waste1 = new Waste("Organico", null, null);
        Waste waste2 = new Waste("Carta", null, null);

        OneTimeSchedule oneTime1 = new OneTimeSchedule(customer1, waste1,
                LocalDate.now().plusDays(1));

        RecurringSchedule recurring = new RecurringSchedule(customer2, waste2,
                LocalDate.now(), RecurringSchedule.Frequency.WEEKLY);
        recurring.setNextCollectionDate(
                LocalDate.now().plusDays(daysUntilNextCollection));

        Collection collection1 = new Collection(oneTime1);
        Collection collection2 = new Collection(recurring);

        List<Collection> collections = List.of(collection1, collection2);

        getTripManager().createTrip("40100", vehicle,
                List.of(operator1, operator2), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING, collections);

        List<Trip> trips = getTripDAO().findAll();
        assertEquals(1, trips.size());

        Trip trip = trips.get(0);
        assertEquals("40100", trip.getPostalCodes());
        assertEquals(operator1.getName(), trip.getOperators().get(0).getName());
        assertEquals(vehicle.getPlate(), trip.getAssignedVehicle().getPlate());
    }
}
