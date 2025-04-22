package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.utils.DateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Location location;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
    }

    @Test
    void testPersistenceAndGetter() {
        em.getTransaction().begin();
        em.persist(location);
        em.persist(customer);
        em.getTransaction().commit();

        Customer found = em.find(Customer.class, customer.getCustomerId());
        assertNotNull(found);
        assertEquals(customer.getName(), found.getName());

        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();

        Customer deleted = em.find(Customer.class, customer.getCustomerId());
        assertNull(deleted);
    }

    @Test
    public void testGetInfo() {
        em.getTransaction().begin();
        em.persist(location);
        em.persist(customer);
        em.getTransaction().commit();
        String expectedInfo = String.format(
            "Name: %s, Surname: %s, Address: %s, Email: %s, Phone: %s, CustomerId: %d",
            "Mario", 
            "Rossi", 
            location.toString(),
            "mario.rossi@example.com", 
            "1234567890", 
            customer.getCustomerId()
        );    
        assertEquals(expectedInfo, customer.getInfo());
    }    
}
