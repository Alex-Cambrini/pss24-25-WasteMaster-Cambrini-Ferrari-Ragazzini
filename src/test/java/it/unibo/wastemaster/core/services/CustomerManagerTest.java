package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerManagerTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private String email;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();
        email = "test@test.it";
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, email, "1234567890");

    }

    @Test
    void testAddCustomer() {
        Customer saved = getCustomerManager().addCustomer(customer);
        assertNotNull(saved.getCustomerId());

        // Add customer with duplicate email
        Customer duplicate = new Customer("Mario", "Rossi", location, email, "999999999");
        assertThrows(IllegalArgumentException.class,
                () -> getCustomerManager().addCustomer(duplicate),
                "A customer with this email already exists");
    }

    @Test
    void testGetCustomerById() {
        Customer saved = getCustomerManager().addCustomer(customer);
        int savedId = saved.getCustomerId();

        Customer found = getCustomerManager().getCustomerById(savedId);
        assertNotNull(found);
        assertEquals(saved.getName(), found.getName());
        assertEquals(saved.getSurname(), found.getSurname());
        assertEquals(saved.getEmail(), found.getEmail());
        assertEquals(saved.getPhone(), found.getPhone());
        assertEquals(saved.getLocation().getStreet(), found.getLocation().getStreet());
        assertEquals(saved.getLocation().getCivicNumber(),
                found.getLocation().getCivicNumber());
        assertEquals(saved.getLocation().getCity(), found.getLocation().getCity());
        assertEquals(saved.getLocation().getPostalCode(),
                found.getLocation().getPostalCode());

        // not found
        Customer notFound = getCustomerManager().getCustomerById(-1);
        assertNull(notFound);
    }

    @Test
    void testUpdateCustomer() {
        Customer saved = getCustomerManager().addCustomer(customer);
        String newPhone = "0000000000";
        String newName = "Francesco";

        saved.setPhone(newPhone);
        saved.setName(newName);
        getCustomerManager().updateCustomer(saved);
        Customer updated = getCustomerManager().getCustomerById(saved.getCustomerId());
        assertEquals(newPhone, updated.getPhone());
        assertEquals(newName, updated.getName());
    }

    @Test
    void testSoftDeleteCustomer() {
        Customer saved = getCustomerManager().addCustomer(customer);
        int savedId = saved.getCustomerId();

        assertNotNull(saved);
        assertFalse(saved.isDeleted());

        boolean result = getCustomerManager().softDeleteCustomer(saved);
        assertTrue(result);

        Customer deletedCustomer = getCustomerManager().getCustomerById(savedId);
        assertNotNull(deletedCustomer);
        assertTrue(deletedCustomer.isDeleted());

        assertEquals(savedId, deletedCustomer.getCustomerId());

        assertFalse(getCustomerManager().softDeleteCustomer(null));

        Customer nonExistentCustomer = new Customer("Non", "Existent", location,
                "nonexistent@test.it", "1234567890");
        assertFalse(getCustomerManager().softDeleteCustomer(nonExistentCustomer));
    }

    @Test
    void testAddCustomerInvalid() {
        final Location location1 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithNullEmail =
                new Customer("Mario", "Rossi", location1, null, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithNullEmail));

        final Location location2 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithInvalidEmail =
                new Customer("Mario", "Rossi", location2, "notValidEmail", "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithInvalidEmail));

        final Location location3 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithInvalidPhone =
                new Customer("Mario", "Rossi", location3, email, "notValidPhone");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithInvalidPhone));

        final Location location4 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithShortPhone =
                new Customer("Mario", "Rossi", location4, email, "123");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithShortPhone));

        final Location location5 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithLongPhone =
                new Customer("Mario", "Rossi", location5, email, "12345678901234567");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithLongPhone));

        final Location location6 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithEmptyFirstName =
                new Customer("", "Rossi", location6, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithEmptyFirstName));

        final Location location7 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithEmptyLastName =
                new Customer("Mario", "", location7, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithEmptyLastName));

        final Location location8 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithBlankFirstName =
                new Customer("   ", "Rossi", location8, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithBlankFirstName));

        final Location location9 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithBlankLastName =
                new Customer("Mario", "   ", location9, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithBlankLastName));

        final Location location10 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithNullFirstName =
                new Customer(null, "Rossi", location10, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithNullFirstName));

        final Location location11 = new Location("Via Roma", "10", "Bologna", "40100");
        final Customer customerWithNullLastName =
                new Customer("Mario", null, location11, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithNullLastName));

        final Customer customerWithNullLocation =
                new Customer("Mario", "Rossi", null, email, "1234567890");
        assertThrows(ConstraintViolationException.class,
                () -> getCustomerManager().addCustomer(customerWithNullLocation));
    }

    @Test
    void testGetCustomerInvalidId() {
        final int negativeId = -5;
        final int zeroId = 0;
        final int largeId = 99999;

        assertNull(getCustomerManager().getCustomerById(negativeId));
        assertNull(getCustomerManager().getCustomerById(zeroId));
        assertNull(getCustomerManager().getCustomerById(largeId));
    }

    @Test
    void testUpdateCustomerInvalid() {
        // Test: Customer null
        assertThrows(IllegalArgumentException.class,
                () -> getCustomerManager().updateCustomer(null));

        // Test: Customer not persisted (not saved in DB)
        Customer notPersisted =
                new Customer("Franco", "Neri", location, "test2@test.it", "1234567890");
        assertThrows(IllegalArgumentException.class,
                () -> getCustomerManager().updateCustomer(notPersisted));
    }

    @Test
    void testUpdateCustomerNoChange() {
        getCustomerManager().addCustomer(customer);
        assertDoesNotThrow(() -> getCustomerManager().updateCustomer(customer));
    }

    @Test
    void testGetAllCustomers() {
        assertTrue(getCustomerManager().getAllCustomers().isEmpty());

        getCustomerManager().addCustomer(customer);

        Customer secondCustomer =
                new Customer("Luigi", "Verdi", location, "luigi@test.it", "0987654321");
        getCustomerManager().addCustomer(secondCustomer);

        List<Customer> customers = getCustomerManager().getAllCustomers();
        assertNotNull(customers);
        assertEquals(2, customers.size());

        List<String> emails = customers.stream().map(Customer::getEmail).toList();
        assertTrue(emails.contains(customer.getEmail()));
        assertTrue(emails.contains(secondCustomer.getEmail()));
    }
}
