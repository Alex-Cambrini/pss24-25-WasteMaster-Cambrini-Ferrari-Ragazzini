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

	
}
