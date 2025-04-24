package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;

public class CustomerDAOTest extends AbstractDatabaseTest {

    private Location location;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Test", "1", "Roma", "00100");
        locationDAO = new GenericDAO<>(em, Location.class);
    }

    @Test
    public void testExistsByEmail() {

        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Customer customer = new Customer("Giulia", "Neri", location, existingEmail, "1234567890");

        customerDAO.insert(customer);
        assertTrue(customerDAO.existsByEmail(existingEmail), "Should return true for existing email");
        assertFalse(customerDAO.existsByEmail(nonExistingEmail), "Should return false for non-existing email");
    }
}
