package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest extends AbstractDatabaseTest {
        private Location location;
        private Customer customer;
        private String email;

        @BeforeEach
        public void setUp() {
                super.setUp();
                em.getTransaction().begin();
                email = "test@test.it";
                location = new Location("Via Roma", "10", "Bologna", "40100");
                customer = new Customer("Mario", "Rossi", location, email, "1234567890");

        }
        @Test
        void testAddCustomer() {
                Customer saved = customerManager.addCustomer(customer);
                assertNotNull(saved.getCustomerId());

                // Add customer with duplicate email
                Customer duplicate = new Customer(
                                "Mario", "Rossi",
                                location,
                                email,
                                "999999999");
                assertThrows(
                                IllegalArgumentException.class,
                                () -> customerManager.addCustomer(duplicate),
                                "A customer with this email already exists");
        }

        @Test
        void testGetCustomerById() {
                Customer saved = customerManager.addCustomer(customer);
                int savedId = saved.getCustomerId();

                Customer found = customerManager.getCustomerById(savedId);
                assertNotNull(found);
                assertEquals(saved.getName(), found.getName());
                assertEquals(saved.getSurname(), found.getSurname());
                assertEquals(saved.getEmail(), found.getEmail());
                assertEquals(saved.getPhone(), found.getPhone());
                assertEquals(saved.getLocation().getStreet(), found.getLocation().getStreet());
                assertEquals(saved.getLocation().getCivicNumber(), found.getLocation().getCivicNumber());
                assertEquals(saved.getLocation().getCity(), found.getLocation().getCity());
                assertEquals(saved.getLocation().getPostalCode(), found.getLocation().getPostalCode());

                // not found
                Customer notFound = customerManager.getCustomerById(-1);
                assertNull(notFound);
        }

        @Test
        void testUpdateCustomer() {
                Customer saved = customerManager.addCustomer(customer);
                String newPhone = "0000000000";
                String newName = "Francesco";

                saved.setPhone(newPhone);
                saved.setName(newName);
                customerManager.updateCustomer(saved);
                Customer updated = customerManager.getCustomerById(saved.getCustomerId());
                assertEquals(newPhone, updated.getPhone());
                assertEquals(newName, updated.getName());
        }

        @Test
        void testSoftDeleteCustomer() {
                Customer saved = customerManager.addCustomer(customer);
                int savedId = saved.getCustomerId();

                assertNotNull(saved);
                assertFalse(saved.isDeleted());

                boolean result = customerManager.softDeleteCustomer(saved);
                assertTrue(result);

                Customer deletedCustomer = customerManager.getCustomerById(savedId);
                assertNotNull(deletedCustomer);
                assertTrue(deletedCustomer.isDeleted());

                assertEquals(savedId, deletedCustomer.getCustomerId());

                assertFalse(customerManager.softDeleteCustomer(null));

                Customer nonExistentCustomer = new Customer("Non", "Existent", location, "nonexistent@test.it",
                                "1234567890");
                assertFalse(customerManager.softDeleteCustomer(nonExistentCustomer));
        }

        @Test
        void testAddCustomerInvalid() {
                // Null or invalid email
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(
                                                new Customer("Mario", "Rossi",
                                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                                null, "1234567890")));

                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", "Rossi",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                "notValidEmail", "1234567890")));

                // Invalid phone number
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(
                                                new Customer("Mario", "Rossi",
                                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                                email, "notValidPhone")));

                // Phone number too short
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", "Rossi",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "123")));

                // Phone number too long
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(
                                                new Customer("Mario", "Rossi",
                                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                                email, "12345678901234567")));

                // Empty first name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("", "Rossi",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Empty last name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", "",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Blank first name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("   ", "Rossi",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Blank last name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", "   ",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Null first name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer(null, "Rossi",
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Null last name
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", null,
                                                new Location("Via Roma", "10", "Bologna", "40100"),
                                                email, "1234567890")));

                // Null location
                assertThrows(ConstraintViolationException.class,
                                () -> customerManager.addCustomer(new Customer("Mario", "Rossi",
                                                null, email, "1234567890")));
        }

        @Test
        void testGetCustomerInvalidId() {
                assertNull(customerManager.getCustomerById(-5));
                assertNull(customerManager.getCustomerById(0));
                assertNull(customerManager.getCustomerById(99999));
        }

        @Test
        void testUpdateCustomerInvalid() {
                // Test: Customer null
                assertThrows(IllegalArgumentException.class, () -> customerManager.updateCustomer(null));

                // Test: Customer not persisted (not saved in DB)
                Customer notPersisted = new Customer("Franco", "Neri",
                                new Location("Via Roma", "10", "Bologna", "40100"), "test2@test.it", "1234567890");
                assertThrows(IllegalArgumentException.class, () -> customerManager.updateCustomer(notPersisted));
        }

        @Test
        void testUpdateCustomerNoChange() {
                customerManager.addCustomer(customer);
                assertDoesNotThrow(() -> customerManager.updateCustomer(customer));
        }
}
