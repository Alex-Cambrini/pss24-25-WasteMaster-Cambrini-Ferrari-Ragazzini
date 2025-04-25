package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerManagerTest extends AbstractDatabaseTest {

    @Test
    void testAddCustomer() {
        Customer customer = customerManager.addCustomer("Mario", "Rossi", "mario@example.com", "1234567890",
                "Via Roma", "10", "Bologna", "40100");
        Assertions.assertNotNull(customer.getCustomerId());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            customerManager.addCustomer("Mario", "Rossi", "mario@example.com", "999999999", "Via Roma", "10",
                    "Bologna", "40100");
        });
    }

    @Test
    void testGetCustomerById() {
        Customer customer = customerManager.addCustomer("Luca", "Verdi", "luca@example.com", "1112223333",
                "Via Milano", "15", "Milano", "20100");
        int id = customer.getCustomerId();

        Customer found = customerManager.getCustomerById(id);
        Assertions.assertNotNull(found);
        Assertions.assertEquals("luca@example.com", found.getEmail());

        Customer notFound = customerManager.getCustomerById(9999);
        Assertions.assertNull(notFound);
    }

    @Test
    void testUpdateCustomer() {
        Customer customer = customerManager.addCustomer("Giulia", "Bianchi", "giulia@example.com", "4445556667",
                "Via Torino", "5", "Torino", "10100");
        customer.setPhone("0000000000");

        customerManager.updateCustomer(customer);

        Customer updated = customerManager.getCustomerById(customer.getCustomerId());
        Assertions.assertEquals("0000000000", updated.getPhone());
    }

    @Test
    void testDeleteCustomer() {
        Customer customer = customerManager.addCustomer("Elena", "Neri", "elena@example.com", "8889997776",
                "Via Napoli", "8", "Napoli", "80100");
        int id = customer.getCustomerId();

        boolean deleted = customerManager.deleteCustomer(customer);
        Assertions.assertTrue(deleted);

        Customer afterDelete = customerManager.getCustomerById(id);
        Assertions.assertNull(afterDelete);

        Assertions.assertFalse(customerManager.deleteCustomer(null));
    }

    @Test
    void testAddCustomerInvalid() {
        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("Anna", "Blu", null, "1234567890", "Via", "1", "Roma", "00100"));

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("Anna", "Blu", "anna@example.com",
                        "abcde123", "Via", "1", "Roma", "00100"));

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("", "Blu", "vuoto@example.com",
                        "1234567890", "Via", "1", "Roma", "00100"));

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("Anna", "Blu", "email-sbagliato",
                        "1234567890", "Via", "1", "Roma", "00100"));

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("Anna", "Blu", "posta@example.com",
                        "1234567890", "Via", "1", "Roma", "00A00"));

        Assertions.assertThrows(ConstraintViolationException.class,
                () -> customerManager.addCustomer("Anna", "Blu", "civico@example.com",
                        "1234567890", "Via", "", "Roma", "00100"));
    }

    @Test
    void testAddCustomerDuplicateEmail() {
        customerManager.addCustomer("Test", "Dup", "dup@example.com", "1234567890", "Via", "1", "Roma", "00100");

        Assertions.assertThrows(IllegalArgumentException.class, () -> customerManager.addCustomer("Altro", "Dup",
                "dup@example.com", "0987654321", "Via", "1", "Roma", "00100"));
    }

    @Test
    void testGetCustomerInvalidId() {
        Assertions.assertNull(customerManager.getCustomerById(-5));
        Assertions.assertNull(customerManager.getCustomerById(0));
        Assertions.assertNull(customerManager.getCustomerById(99999));
    }
}
