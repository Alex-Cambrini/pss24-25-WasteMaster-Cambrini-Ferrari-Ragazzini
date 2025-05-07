package it.unibo.wastemaster.core.models;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.models.Employee.LicenceType;
import it.unibo.wastemaster.core.models.Employee.Role;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    private Trip trip;
    private Vehicle vehicle;
    private List<Employee> operators;
    private List<String> postalCodes;
    private LocalDateTime departure;
    private LocalDateTime returnTime;

   
    @BeforeEach
    void setUp() {
        
        vehicle = new Vehicle("ABC123", "Truck", null, 5000, null, null);

        
        Employee operator1 = new Employee("Mario", "Rossi", null, "mario@waste.it", "1234567890", Role.OPERATOR, LicenceType.C);
        Employee operator2 = new Employee("Luigi", "Verdi", null, "luigi@waste.it", "0987654321", Role.OPERATOR, LicenceType.C);

        operators = Arrays.asList(operator1, operator2);
        postalCodes = Arrays.asList("40121", "40122");

        departure = LocalDateTime.of(2025, 5, 10, 8, 0);
        returnTime = LocalDateTime.of(2025, 5, 10, 12, 0);

        trip = new Trip(1, postalCodes, vehicle, operators, departure, returnTime, Trip.TripStatus.PENDING);
    }

   

}
