package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Trip model.
 */
class TripTest extends AbstractDatabaseTest {

    private Trip trip;
    private Vehicle vehicle;
    private Employee operator;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    /**
     * Set up test data.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        final int fiveDaysAgo = 5;
        getEntityManager().getTransaction().begin();

        final int vehicleYear = 2020;
        final int vehicleCapacity = 5;

        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", vehicleYear,
                Vehicle.RequiredLicence.C1,
                Vehicle.VehicleStatus.IN_SERVICE, vehicleCapacity);
        operator = new Employee("John", "Doe",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "john.doe@example.com", "1234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);

        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(fiveDaysAgo);
    }

    /**
     * Test getter and setter methods.
     */
    @Test
    void testGetterSetter() {
        trip = new Trip("40100", vehicle, List.of(operator), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING,
                Collections.emptyList());

        trip.setPostalCodes("40200");
        assertEquals("40200", trip.getPostalCodes());

        trip.setDepartureTime(departureTime);
        assertEquals(departureTime, trip.getDepartureTime());

        trip.setExpectedReturnTime(expectedReturnTime);
        assertEquals(expectedReturnTime, trip.getExpectedReturnTime());

        trip.setStatus(Trip.TripStatus.COMPLETED);
        assertEquals(Trip.TripStatus.COMPLETED, trip.getStatus());

        trip.setOperators(List.of(operator));
        assertEquals(1, trip.getOperators().size());
    }

    /**
     * Test toString method.
     */
    @Test
    void testToString() {
        trip = new Trip("40100", vehicle, List.of(operator), departureTime,
                expectedReturnTime, Trip.TripStatus.PENDING,
                Collections.emptyList());

        String toStringOutput = trip.toString();

        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("Trip"));
        assertTrue(toStringOutput.contains("PostalCode: 40100"));
        assertTrue(toStringOutput.contains(vehicle.getPlate()));
        assertTrue(toStringOutput.contains(departureTime.toString()));
        assertTrue(toStringOutput.contains(expectedReturnTime.toString()));
        assertTrue(toStringOutput.contains(trip.getStatus().name()));
    }

    /**
     * Test persistence of trip.
     */
    @Test
    void testPersistence() {
        final int daysUntilNextCollection = 7;
        final int hoursUntilReturn = 5;

        Employee operator1 = new Employee("John", "Doe",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "john.doe@example.com", "1234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);

        Employee operator2 = new Employee("Anna", "Rossi",
                new Location("Via Milano", "22", "Milano", "20100"),
                "anna.rossi@example.com", "0987654321",
                Employee.Role.OPERATOR, Employee.Licence.C1);

        Employee operator3 = new Employee("Luca", "Bianchi",
                new Location("Via Napoli", "5", "Napoli", "80100"),
                "luca.bianchi@example.com", "1122334455",
                Employee.Role.OPERATOR, Employee.Licence.C1);

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

        getEmployeeDAO().insert(operator1);
        getEmployeeDAO().insert(operator2);
        getEmployeeDAO().insert(operator3);

        getVehicleDAO().insert(vehicle);

        List<Employee> operators = List.of(operator1, operator2, operator3);

        Trip createdTrip = new Trip("40100", vehicle, operators,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(hoursUntilReturn),
                Trip.TripStatus.PENDING, collections);

        getTripDAO().insert(createdTrip);

        Trip found = getTripDAO().findById(createdTrip.getTripId());
        assertNotNull(found);
        assertEquals(createdTrip.getPostalCodes(), found.getPostalCodes());

        int foundId = found.getTripId();
        getTripDAO().delete(found);

        Trip deleted = getTripDAO().findById(foundId);
        assertNull(deleted);
    }
}
