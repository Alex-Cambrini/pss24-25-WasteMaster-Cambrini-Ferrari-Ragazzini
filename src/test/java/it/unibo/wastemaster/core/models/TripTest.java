package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.dao.TripDAO;
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
    // private TripDAO tripDAO;

    @BeforeEach
    public void setUp() {
        super.setUp();
        vehicle = new Vehicle("AB123CD", "Iveco", "Daily", 2020, Vehicle.LicenceType.C1, Vehicle.VehicleStatus.IN_SERVICE);
        operator = new Employee("John", "Doe", new Location("Via Roma", "10", "Bologna", "40100"), "john.doe@example.com", "1234567890", Employee.Role.OPERATOR, Employee.LicenceType.C1);
        departureTime = LocalDateTime.now().plusHours(1);
        expectedReturnTime = departureTime.plusHours(5);
        assertNotNull(vehicle);
        assertNotNull(operator);
        // assertNotNull(em, "EntityManager should be initialized!");
        // tripDAO = new TripDAO(em);
        // assertNotNull(tripDAO, "TripDAO should be initialized!");
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

     
   @Test
    public void testToString() {
    trip = new Trip(1, "40100", vehicle, List.of(operator), departureTime, expectedReturnTime, Trip.TripStatus.PENDING, null);
    
    String toStringOutput = trip.toString();

    assertNotNull(toStringOutput);
    assertTrue(toStringOutput.contains("Trip")); 
    assertTrue(toStringOutput.contains("ID: " + trip.getTripId())); 
    assertTrue(toStringOutput.contains(trip.getPostalCodes())); 
    assertTrue(toStringOutput.contains(vehicle != null ? vehicle.getPlate() : "N/A")); 
    assertTrue(toStringOutput.contains(departureTime.toString())); 
    assertTrue(toStringOutput.contains(expectedReturnTime.toString())); 
    assertTrue(toStringOutput.contains(trip.getStatus().name())); 
    }

    // to fix
    //  @Test
    //     public void testPersistence() {
    //     em.getTransaction().begin();   
    //     tripDAO.insert(trip);  
    //     Trip found = em.find(Trip.class, trip.getTripId());
    //     assertNotNull(found);
    //     assertEquals(trip.getPostalCodes(), found.getPostalCodes());
    //     tripDAO.delete(trip);
    //     Trip deleted = em.find(Trip.class, trip.getTripId());
    //     assertNull(deleted);
    //     em.getTransaction().commit();

    // }

}