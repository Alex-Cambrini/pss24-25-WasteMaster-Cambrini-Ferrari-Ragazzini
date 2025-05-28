package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Location location;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890");
    }

    @Test
    void testPersistenceAndGetter() {
        getCustomerDAO().insert(customer);
        int customerId = customer.getCustomerId();
        Customer found = getCustomerDAO().findById(customerId);
        assertNotNull(found);
        assertEquals(customer.getName(), found.getName());
        getCustomerDAO().delete(customer);
        Customer deleted = getCustomerDAO().findById(customerId);
        assertNull(deleted);
    }

    @Test
    void testToString() {
        String toStringOutput = customer.toString();
        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("Customer"));
        assertTrue(toStringOutput.contains(customer.getName()));
        assertTrue(toStringOutput.contains(customer.getSurname()));
        assertTrue(toStringOutput.contains(customer.getEmail()));
        assertTrue(toStringOutput.contains(customer.getPhone()));
        assertTrue(toStringOutput.contains(location.toString()));
    }
}
