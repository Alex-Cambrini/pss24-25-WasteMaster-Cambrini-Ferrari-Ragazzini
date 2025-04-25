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

}
