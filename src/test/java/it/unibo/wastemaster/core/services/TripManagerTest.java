package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Trip;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripManagerTest extends AbstractDatabaseTest {

    private TripManager tripManager;
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
        tripManager = new TripManager(em);  
    
    
    }
}