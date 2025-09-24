package it.unibo.wastemaster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.util.Optional;
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
        Optional<Customer> foundOpt = getCustomerDAO().findById(customerId);
        assertTrue(foundOpt.isPresent());
        Customer found = foundOpt.get();
        assertEquals(customer.getName(), found.getName());
        getCustomerDAO().delete(customer);
        Optional<Customer> deletedOpt = getCustomerDAO().findById(customerId);
        assertTrue(deletedOpt.isEmpty());
    }

    @Test
    void testToString() {
        String toStringOutput = customer.toString();
        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains(customer.getName()));
        assertTrue(toStringOutput.contains(customer.getSurname()));
        assertTrue(toStringOutput.contains(customer.getEmail()));
    }
}
