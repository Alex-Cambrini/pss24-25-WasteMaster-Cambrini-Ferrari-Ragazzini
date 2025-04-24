package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Location location;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
    }

    @Test
    void testPersistenceAndGetter() {
        customerDAO.insert(customer);
        int customerId = customer.getCustomerId();
        Customer found = customerDAO.findById(customerId);
        assertNotNull(found);
        assertEquals(customer.getName(), found.getName());
        customerDAO.delete(customer);
        Customer deleted = customerDAO.findById(customerId);
        assertNull(deleted);
    }

    @Test
    public void testGetInfo() {
        String expectedInfo = String.format(
            "Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s, CustomerId: %d",
            "Mario", 
            "Rossi", 
            location.toString(),
            "mario.rossi@example.com", 
            "1234567890", 
            customer.getCustomerId()
        );    
        assertEquals(expectedInfo, customer.getInfo());
    }    
}
