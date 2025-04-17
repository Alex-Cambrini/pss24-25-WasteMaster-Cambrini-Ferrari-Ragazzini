package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.models.Customer;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest extends AbstractDatabaseTest {

    private CustomerManager customerManager;

    @BeforeEach
    void initManager() {
        customerManager = new CustomerManager(new CustomerDAO(em));
    }

    private String generateUniqueEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    void testAddAndGetCustomer() {
        String email = generateUniqueEmail("add");
        Customer customer = customerManager.addCustomer("Test", "User", email, "1234567890",
                "Via Roma", "10", "Bologna", "40100");

        Customer found = customerManager.getCustomerById(customer.getCustomerId());
        assertNotNull(found);
        assertEquals(email, found.getEmail());

        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();
    }

    @Test
    void testEmailDuplicationCheck() {
    String email = generateUniqueEmail("dup");
    Customer customer = customerManager.addCustomer("Test", "Dup", email,
    "1234567890",
    "Via Roma", "10", "Bologna", "40100");

    assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("Test", "Dup", email, "1234567890",
    "Via Roma", "10", "Bologna", "40100"));

    em.getTransaction().begin();
    em.remove(customer);
    em.getTransaction().commit();
    }

    @Test
    void testUpdateCustomer() {
    customer.setPhone("0000000000");
    customerManager.updateCustomer(customer);

    Customer updated = customerManager.getCustomerById(customer.getCustomerId());
    assertNotNull(updated);
    assertEquals("0000000000", updated.getPhone());
    }

    @Test
    void testDeleteCustomer() {
    customerManager.deleteCustomer(customer);
    Customer deleted = customerManager.getCustomerById(customer.getCustomerId());
    assertNull(deleted);
    }

    @Test
    void testAddCustomerWithInvalidInputs() {
    String validPhone = "1234567890";
    String validStreet = "Via Roma";
    String validCivic = "10";
    String validCity = "Bologna";
    String validPostal = "40100";

    assertAll(
    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("Mario", "Rossi", null, validPhone, validStreet,
    validCivic, validCity, validPostal)
    ),

    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("Mario", "Rossi", "", validPhone, validStreet,
    validCivic, validCity, validPostal)
    ),

    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("Mario", "Rossi", " ", validPhone, validStreet,
    validCivic, validCity, validPostal)
    ),

    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer(null, "Rossi", "test1@example.com", validPhone,
    validStreet, validCivic, validCity, validPostal)
    ),

    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("", "Rossi", "test2@example.com", validPhone,
    validStreet, validCivic, validCity, validPostal)
    ),

    () -> assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer(" ", "Rossi", "test3@example.com", validPhone,
    validStreet, validCivic, validCity, validPostal)
    )
    );
    }

    @Test
    void testDuplicateEmailThrowsException() {
    String email = generateUniqueEmail("dupCase");
    Customer c = customerManager.addCustomer("Luca", "Bianchi", email,
    "1234567890", "Via Roma", "10", "Bologna", "40100");

    assertThrows(IllegalArgumentException.class, () ->
    customerManager.addCustomer("Luca", "Bianchi", email, "1234567890", "Via Roma", "10", "Bologna", "40100"));

    em.getTransaction().begin();
    em.remove(c);
    em.getTransaction().commit();
    }

    @Test
    void testUpdateNonExistentCustomer() {
    Customer fake = new Customer("Ghost", "User", null, "ghost@example.com",
    "0000000000");
    fake.setCustomerId(-999);
    assertDoesNotThrow(() -> customerManager.updateCustomer(fake));
    }

    @Test
    void testDeleteAlreadyDeletedCustomer() {
    String email = generateUniqueEmail("deleteTwice");
    Customer toDelete = customerManager.addCustomer("Anna", "Neri", email,
    "3333333333", "Via Roma", "10", "Bologna", "40100");

    customerManager.deleteCustomer(toDelete);
    assertDoesNotThrow(() -> customerManager.deleteCustomer(toDelete));
    }

    @Test
    void testGetCustomerByInvalidIdReturnsNull() {
    Customer result = customerManager.getCustomerById(-1);
    assertNull(result);
    }
}
