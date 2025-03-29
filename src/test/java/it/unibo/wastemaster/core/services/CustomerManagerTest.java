package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerManagerTest {

	private CustomerManager customerManager;

	@BeforeEach
	void setUp() {
		customerManager = new CustomerManager();
	}

	private String generateUniqueEmail(String prefix) {
		return prefix + "_" + System.currentTimeMillis() + "@example.com";
	}
    @Test
	void testAddAndGetCustomer() {
		String email = generateUniqueEmail("add");
		Customer added = customerManager.addCustomer(
			"Test", "User", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		Customer found = customerManager.getCustomerById(added.getCustomerId());
		assertNotNull(found);
		assertEquals(email, found.getEmail());
	}

    @Test
	void testEmailDuplicationCheck() {
		String email = generateUniqueEmail("dup");
		customerManager.addCustomer(
			"Test", "Dup", email, "1234567890",
			"Via Roma", "10", "Bologna", "40100"
		);

		assertTrue(customerManager.existsByEmail(email));
	}

	

	
}
