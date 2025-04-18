package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private Customer customer;
    private Location location;

    @BeforeEach
    public void setUp() {
        location = new Location("Via Roma", "1", "Milano", "Italy");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
    }

    @Test
    public void testGetCustomerId() {
        customer.setCustomerId(100);
        assertEquals(100, customer.getCustomerId());
    }

    @Test
    public void testSetCustomerId() {
        customer.setCustomerId(200);
        assertEquals(200, customer.getCustomerId());
    }

    @Test
    public void testGetInfo() {
        customer.setCustomerId(300);
        String expectedInfo = String.format(
            "Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s, CustomerId: %d",
            "Mario", 
            "Rossi", 
            location, 
            "mario.rossi@example.com", 
            "1234567890", 
            300
        );
        assertEquals(expectedInfo, customer.getInfo());
    }
}
