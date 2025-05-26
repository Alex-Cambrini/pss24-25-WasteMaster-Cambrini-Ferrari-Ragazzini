package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    public void testFindByEmail() {
        String existingEmail = "test@example.com";
        String nonExistingEmail = "nonexistent@example.com";

        Customer customer = new Customer("Giulia", "Neri", location, existingEmail, "1234567890");
        customerDAO.insert(customer);

        Customer foundCustomer = customerDAO.findByEmail(existingEmail);
        assertNotNull(foundCustomer, "Customer should be found for existing email");
        assertEquals(existingEmail, foundCustomer.getEmail(), "Email should match");

        Customer notFoundCustomer = customerDAO.findByEmail(nonExistingEmail);
        assertNull(notFoundCustomer, "Customer should not be found for non-existing email");
    }

    @Test
    public void testFindCustomerDetails() {
        Customer customer1 = new Customer("Marco", "Verdi", location, "marco@example.com", "1111111111");
        Customer customer2 = new Customer("Luca", "Bianchi", location, "luca@example.com", "2222222222");
        Customer deletedCustomer = new Customer("Laura", "Rossi", location, "laura@example.com", "3333333333");

        customerDAO.insert(customer1);
        customerDAO.insert(customer2);
        customerDAO.insert(deletedCustomer);
        deletedCustomer.delete();
        customerDAO.update(deletedCustomer);

        var result = customerDAO.findCustomerDetails();

        assertTrue(result.stream().anyMatch(c -> c.getEmail().equals("marco@example.com")), "Marco should be present");
        assertTrue(result.stream().anyMatch(c -> c.getEmail().equals("luca@example.com")), "Luca should be present");
        assertFalse(result.stream().anyMatch(c -> c.getEmail().equals("laura@example.com")),
                "Laura (deleted) should not be present");
    }
}
