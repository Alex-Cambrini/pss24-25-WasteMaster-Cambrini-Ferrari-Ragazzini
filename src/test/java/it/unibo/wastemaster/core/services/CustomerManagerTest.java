package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
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

        Optional<Customer> found = getCustomerManager().getCustomerById(savedId);
        assertTrue(found.isPresent());
        Customer foundCustomer = found.get();

        assertEquals(saved.getName(), foundCustomer.getName());
        assertEquals(saved.getSurname(), foundCustomer.getSurname());
        assertEquals(saved.getEmail(), foundCustomer.getEmail());
        assertEquals(saved.getPhone(), foundCustomer.getPhone());
        assertEquals(saved.getLocation().getStreet(),
                foundCustomer.getLocation().getStreet());
        assertEquals(saved.getLocation().getCivicNumber(),
                foundCustomer.getLocation().getCivicNumber());
        assertEquals(saved.getLocation().getCity(),
                foundCustomer.getLocation().getCity());
        assertEquals(saved.getLocation().getPostalCode(),
                foundCustomer.getLocation().getPostalCode());

        Optional<Customer> notFound = getCustomerManager().getCustomerById(-1);
        assertFalse(notFound.isPresent());
    }

    @Test
    void testUpdateCustomer() {
        Customer saved = getCustomerManager().addCustomer(customer);
        String newPhone = "0000000000";
        String newName = "Francesco";

        saved.setPhone(newPhone);
        saved.setName(newName);
        getCustomerManager().updateCustomer(saved);
        Optional<Customer> updated =
                getCustomerManager().getCustomerById(saved.getCustomerId());
        assertTrue(updated.isPresent());
        Customer updatedCustomer = updated.get();

        assertEquals(newPhone, updatedCustomer.getPhone());
        assertEquals(newName, updatedCustomer.getName());
    }

    @Test
    void testSoftDeleteCustomer() {
        Customer saved = getCustomerManager().addCustomer(customer);
        int savedId = saved.getCustomerId();

        assertFalse(saved.isDeleted());

        boolean result = getCustomerManager().softDeleteCustomer(saved);
        assertTrue(result);

        Optional<Customer> deletedCustomer =
                getCustomerManager().getCustomerById(savedId);
        assertTrue(deletedCustomer.isPresent());
        Customer deleted = deletedCustomer.get();
        assertTrue(deleted.isDeleted());
        assertEquals(savedId, deleted.getCustomerId());

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

        assertTrue(getCustomerManager().getCustomerById(negativeId).isEmpty());
        assertTrue(getCustomerManager().getCustomerById(zeroId).isEmpty());
        assertTrue(getCustomerManager().getCustomerById(largeId).isEmpty());
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
        List<Customer> initialCustomers = getCustomerManager().getAllCustomers();
        initialCustomers.forEach(
                c -> getCustomerManager().softDeleteCustomer(c)); // se supportato

        assertTrue(getCustomerManager().getAllCustomers().isEmpty());

        getCustomerManager().addCustomer(customer);

        Customer secondCustomer =
                new Customer("Luigi", "Verdi", location, "luigi@test.it", "0987654321");
        getCustomerManager().addCustomer(secondCustomer);

        List<Customer> customers = getCustomerManager().getAllCustomers();
        assertEquals(2, customers.size());

        List<String> emails = customers.stream().map(Customer::getEmail).toList();
        assertTrue(emails.contains(customer.getEmail()));
        assertTrue(emails.contains(secondCustomer.getEmail()));
    }
}
