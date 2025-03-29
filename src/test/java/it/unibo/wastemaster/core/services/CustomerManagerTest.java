package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest {

	private CustomerManager customerManager;
    private Customer testCustomer;

	@BeforeEach
	void setUp() {
		customerManager = new CustomerManager();
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
}
