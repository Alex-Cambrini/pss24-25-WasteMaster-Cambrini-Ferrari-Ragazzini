package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest extends AbstractDatabaseTest {

    private String email;
    private String phoneNumber;

    @BeforeEach
    public void setUp() {
        super.setUp();
        em.getTransaction().begin();
        email = "test@test.com";
        phoneNumber = "1234567890";
    }

    @Test
    void testAddAndGetCustomer() {
        Customer customer = customerManager.addCustomer("Test", "User", email, phoneNumber,
                "Via Roma", "10", "Bologna", "40100");
        Customer found = customerManager.getCustomerById(customer.getCustomerId());
        assertNotNull(found);
        assertEquals(email, found.getEmail());
    }

    @Test
    void testEmailDuplicationCheck() {
        customerManager.addCustomer("Test", "Dup", email, phoneNumber, "Via Roma", "10", "Bologna", "40100");
        assertThrows(IllegalArgumentException.class,
                () -> customerManager.addCustomer("Test", "Dup", email, phoneNumber,
                        "Via Roma", "10", "Bologna", "40100"));
    }

    @Test
    void testUpdateCustomer() {
        Customer customer = customerManager.addCustomer("Test", "Dup", email, phoneNumber, "Via Roma", "10", "Bologna",
                "40100");
        customer.setPhone(phoneNumber);
        customerManager.updateCustomer(customer);
        Customer updated = customerManager.getCustomerById(customer.getCustomerId());
        assertNotNull(updated);
        assertEquals(phoneNumber, updated.getPhone());
    }

    @Test
    void testDeleteCustomer() {
        Customer customer = customerManager.addCustomer("Test", "Dup", email, phoneNumber, "Via Roma", "10", "Bologna",
                "40100");
        customerManager.deleteCustomer(customer);
        Customer deleted = customerManager.getCustomerById(customer.getCustomerId());
        assertNull(deleted);
    }

    @Test
    void testAddCustomerWithInvalidInputs() {
        String validName = "Mario";
        String validSurname = "Rossi";
        String validEmail = "valid@mail.it";
        String validPhone = "1234567890";
        String validStreet = "Via Roma";
        String validCivic = "10";
        String validCity = "Bologna";
        String validPostal = "40100";

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer(validName, validSurname, null, validPhone, validStreet,
                                validCivic, validCity, validPostal)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer(validName, validSurname, "", validPhone, validStreet,
                                validCivic, validCity, validPostal)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer(validName, validSurname, " ", validPhone, validStreet,
                                validCivic, validCity, validPostal)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer(null, validSurname, validEmail, validPhone,
                                validStreet, validCivic, validCity, validPostal)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer("", validSurname, validEmail, validPhone,
                                validStreet, validCivic, validCity, validPostal)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> customerManager.addCustomer(" ", validSurname, validEmail, validPhone,
                                validStreet, validCivic, validCity, validPostal)));
    }

    @Test
    void testDeleteAlreadyDeletedCustomer() {
        Customer toDelete = customerManager.addCustomer("Luca", "Bianchi", email,
        phoneNumber, "Via Roma", "10", "Bologna", "40100");

        customerManager.deleteCustomer(toDelete);
        assertDoesNotThrow(() -> customerManager.deleteCustomer(toDelete));
    }

    @Test
    void testGetCustomerByInvalidIdReturnsNull() {
        Customer result = customerManager.getCustomerById(-1);
        assertNull(result);
    }
}
