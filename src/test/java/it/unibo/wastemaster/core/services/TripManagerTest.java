package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
        employeeDAO.insert(new Employee("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"),
                "john.doe@example.com", "+391234567890", Employee.Role.OPERATOR, Employee.LicenceType.C1));
        employeeDAO.insert(new Employee("Anna", "Rossi", new Location("Via Milano", "22", "Milano", "20100"),
                "anna.rossi@example.com", "+390987654321", Employee.Role.OPERATOR, Employee.LicenceType.C1));

        operator1 = employeeDAO.findAll().get(0);
        operator2 = employeeDAO.findAll().get(1);

        vehicleDAO.insert(vehicle);

        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(5);
    }

    // @Test
    // void testCreateAndGetTrip() {
    //     tripManager.createTrip("40100", vehicle, List.of(operator1, operator2),
    //             departureTime, expectedReturnTime, Trip.TripStatus.PENDING);

    //     List<Trip> allTrips = tripDAO.findAll();
    //     assertFalse(allTrips.isEmpty());

    //     Trip savedTrip = allTrips.get(0);
    //     assertEquals("40100", savedTrip.getPostalCodes());
    //     assertEquals(vehicle.getPlate(), savedTrip.getAssignedVehicle().getPlate());
    //     assertEquals(2, savedTrip.getOperators().size());
    //     assertEquals(departureTime, savedTrip.getDepartureTime());
    //     assertEquals(expectedReturnTime, savedTrip.getExpectedReturnTime());
    //     assertEquals(Trip.TripStatus.PENDING, savedTrip.getStatus());

    //     Trip found = tripManager.getTripById(savedTrip.getTripId());
    //     assertNotNull(found);
    //     assertEquals(savedTrip.getTripId(), found.getTripId());
    // }

   
}
