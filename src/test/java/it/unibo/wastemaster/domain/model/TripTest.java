package it.unibo.wastemaster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for Trip model.
 */
class TripTest extends AbstractDatabaseTest {

    private Trip trip;
    private Vehicle vehicle;
    private Location location;
    private Employee operator1;
    private Employee operator2;
    private Employee operator3;
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

        final int vehicleYear = 2020;
        final int vehicleCapacity = 5;

        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", vehicleYear,
                Vehicle.RequiredLicence.C1,
                Vehicle.VehicleStatus.IN_SERVICE, vehicleCapacity);
        location = new Location("Via Roma", "10", "Bologna", "40100");
        operator1 = new Employee("John", "Doe", location,
                "john.doe@example.com", "1234567890",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        operator2 = new Employee("Jane", "Doe", location, "jane.doe@example.com",
                "0987654321",
                Employee.Role.OPERATOR, Employee.Licence.C1);
        operator3 = new Employee("Alice", "Smith", location, "alice.smith@example.com",
                "1122334455",
                Employee.Role.OPERATOR, Employee.Licence.C1);

        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(fiveDaysAgo);
    }

    /**
     * Test getter and setter methods.
     */
    @Test
    void testGetterSetter() {
        trip = new Trip("40100", vehicle, List.of(operator1, operator2, operator3),
                departureTime,
                expectedReturnTime,
                Collections.emptyList());

        trip.setPostalCode("40200");
        assertEquals("40200", trip.getPostalCode());

        trip.setDepartureTime(departureTime);
        assertEquals(departureTime, trip.getDepartureTime());

        trip.setExpectedReturnTime(expectedReturnTime);
        assertEquals(expectedReturnTime, trip.getExpectedReturnTime());

        trip.setStatus(Trip.TripStatus.COMPLETED);
        assertEquals(Trip.TripStatus.COMPLETED, trip.getStatus());

        trip.setOperators(List.of(operator1, operator2));
        List<Employee> operators = trip.getOperators();
        assertEquals(2, operators.size());
        assertTrue(operators.contains(operator1));
        assertTrue(operators.contains(operator2));
    }

    /**
     * Test persistence of trip.
     */
    @Test
    void testPersistence() {
        final int daysUntilNextCollection = 7;
        final int hoursUntilReturn = 5;

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
                LocalDateTime.now().plusHours(hoursUntilReturn), collections);

        getTripDAO().insert(createdTrip);

        Optional<Trip> foundOpt = getTripDAO().findById(createdTrip.getTripId());
        assertTrue(foundOpt.isPresent());

        Trip found = foundOpt.get();
        assertEquals(createdTrip.getPostalCode(), found.getPostalCode());

        int foundId = found.getTripId();
        getTripDAO().delete(found);

        Optional<Trip> deletedOpt = getTripDAO().findById(foundId);
        assertTrue(deletedOpt.isEmpty());
    }

    @Test
    void testDefaultStatusActive() {
        Trip t = new Trip("40100", vehicle, List.of(operator1), departureTime,
                expectedReturnTime,
                Collections.emptyList());
        assertEquals(Trip.TripStatus.ACTIVE, t.getStatus());
    }

    @Test
    void testValidationTripFields() {
        Trip invalid = new Trip();
        var violations =
                it.unibo.wastemaster.infrastructure.utils.ValidateUtils.VALIDATOR.validate(
                        invalid);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("departureTime")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString()
                        .equals("expectedReturnTime")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("status")));
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("collections")));
    }

    @Test
    void testUpdateOperatorsAndCollectionsPersistence() {
        getEmployeeDAO().insert(operator1);
        getEmployeeDAO().insert(operator2);
        getEmployeeDAO().insert(operator3);
        getVehicleDAO().insert(vehicle);

        Customer customer = new Customer("Mario", "Rossi", location,
                "mario.rossi@example.com", "1234567890");
        getCustomerDAO().insert(customer);

        Waste waste = new Waste("Organico", true, false);
        getWasteDAO().insert(waste);

        OneTimeSchedule schedule =
                new OneTimeSchedule(customer, waste, LocalDate.now().plusDays(1));
        getOneTimeScheduleDAO().insert(schedule);

        Collection collection = new Collection(schedule);
        getCollectionDAO().insert(collection);

        Trip trip = new Trip("40100", vehicle,
                new ArrayList<>(List.of(operator1, operator2)),
                departureTime, expectedReturnTime,
                new ArrayList<>(List.of(collection)));
        getTripDAO().insert(trip);

        Trip reloaded = getTripDAO().findById(trip.getTripId()).orElseThrow();
        reloaded.getOperators().clear();
        reloaded.getOperators().add(operator3);
        getTripDAO().update(reloaded);

        Trip reloaded2 = getTripDAO().findById(trip.getTripId()).orElseThrow();
        reloaded2.getCollections().clear();
        reloaded2.getCollections().add(collection);
        getTripDAO().update(reloaded2);

        Trip found = getTripDAO().findById(trip.getTripId()).orElseThrow();
        assertEquals(1, found.getOperators().size());
        assertTrue(found.getOperators().contains(operator3));

        assertEquals(1, found.getCollections().size());
        assertTrue(found.getCollections().contains(collection));
    }
}
