package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest {

	private CustomerManager customerManager;
    private Customer testCustomer;
    private EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
        emf = Persistence.createEntityManagerFactory("test-pu");
		customerManager = new CustomerManager(emf);
        testCustomer = null;
	}

    @AfterEach
	void tearDown() {
		if (testCustomer != null) {
			Customer found = customerManager.getCustomerById(testCustomer.getCustomerId());
			if (found != null) {
				customerManager.deleteCustomer(found);
			}
		}
        if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	private String generateUniqueEmail(String prefix) {
		return prefix + "_" + System.currentTimeMillis() + "@example.com";
	}
    @Test
	void testAddAndGetCustomer() {
		String email = generateUniqueEmail("add");
		testCustomer = customerManager.addCustomer(
			"Test", "User", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		Customer found = customerManager.getCustomerById(testCustomer.getCustomerId());
		assertNotNull(found);
		assertEquals(email, found.getEmail());
	}

    @Test
	void testEmailDuplicationCheck() {
		String email = generateUniqueEmail("dup");
		testCustomer = customerManager.addCustomer(
			"Test", "Dup", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		assertTrue(customerManager.existsByEmail(email));
	}

	@Test
	void testUpdateCustomer() {
		String email = generateUniqueEmail("update");
		testCustomer  = customerManager.addCustomer(
			"Test", "Update", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		testCustomer.setPhone("0000000000");
		customerManager.updateCustomer(testCustomer);

		Customer updated = customerManager.getCustomerById(testCustomer.getCustomerId());
		assertNotNull(updated);
		assertEquals("0000000000", updated.getPhone());
	}

    @Test
	void testDeleteCustomer() {
		String email = generateUniqueEmail("delete");
		testCustomer = customerManager.addCustomer(
			"Test", "Delete", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		int id = testCustomer.getCustomerId();
		customerManager.deleteCustomer(testCustomer);

		Customer deleted = customerManager.getCustomerById(id);
		assertNull(deleted);

        testCustomer = null;
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
                customerManager.addCustomer("Mario", "Rossi", null, validPhone, validStreet, validCivic, validCity, validPostal)
            ),

            () -> assertThrows(IllegalArgumentException.class, () ->
                customerManager.addCustomer("Mario", "Rossi", "", validPhone, validStreet, validCivic, validCity, validPostal)
            ),

            () -> assertThrows(IllegalArgumentException.class, () ->
                customerManager.addCustomer("Mario", "Rossi", "   ", validPhone, validStreet, validCivic, validCity, validPostal)
            ),

            () -> assertThrows(IllegalArgumentException.class, () ->
                customerManager.addCustomer(null, "Rossi", "test1@example.com", validPhone, validStreet, validCivic, validCity, validPostal)
            ),

            () -> assertThrows(IllegalArgumentException.class, () ->
                customerManager.addCustomer("", "Rossi", "test2@example.com", validPhone, validStreet, validCivic, validCity, validPostal)
            ),

            () -> assertThrows(IllegalArgumentException.class, () ->
                customerManager.addCustomer("   ", "Rossi", "test3@example.com", validPhone, validStreet, validCivic, validCity, validPostal)
            )
        );
    }

    @Test
    void testDuplicateEmailThrowsException() {
        String email = generateUniqueEmail("dupCase");
        testCustomer = customerManager.addCustomer("Luca", "Bianchi", email, "1234567890", "Via Roma", "10", "Bologna", "40100");

        assertThrows(IllegalArgumentException.class, () -> {
            customerManager.addCustomer("Luca", "Bianchi", email, "1234567890", "Via Roma", "10", "Bologna", "40100");
        });
    }

    @Test
    void testUpdateNonExistentCustomer() {
        Customer fake = new Customer("Ghost", "User", null, "ghost@example.com", "0000000000");
        fake.setCustomerId(-999);

        assertDoesNotThrow(() -> customerManager.updateCustomer(fake));
    }

    @Test
    void testDeleteAlreadyDeletedCustomer() {
        String email = generateUniqueEmail("deleteTwice");
        testCustomer = customerManager.addCustomer("Anna", "Neri", email, "3333333333", "Via Roma", "10", "Bologna", "40100");

        customerManager.deleteCustomer(testCustomer);

        assertDoesNotThrow(() -> customerManager.deleteCustomer(testCustomer));

        testCustomer = null;
    }

    @Test
    void testGetCustomerByInvalidIdReturnsNull() {
        Customer result = customerManager.getCustomerById(-1);
        assertNull(result);
    }
}
