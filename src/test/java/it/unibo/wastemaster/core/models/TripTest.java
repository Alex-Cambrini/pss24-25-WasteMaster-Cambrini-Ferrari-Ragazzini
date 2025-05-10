package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class TripTest extends AbstractDatabaseTest {

    private Trip trip;
    private Vehicle vehicle;
    private Employee operator;
    private LocalDateTime departureTime;
    private LocalDateTime expectedReturnTime;

    @BeforeEach
    public void setUp() {
        super.setUp();
        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
        operator = new Employee("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890", Employee.Role.OPERATOR, Employee.LicenceType.C1);
        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(5);
        assertNotNull(vehicle);
        assertNotNull(operator);
    }

   
    @Test
        public void testGetterSetter() {
        
        trip = new Trip(1, "40100", vehicle, List.of(operator), departureTime, expectedReturnTime, Trip.TripStatus.PENDING, null);
        
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

    

}