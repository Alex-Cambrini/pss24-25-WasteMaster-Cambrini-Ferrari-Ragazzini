package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;

public class GenericDAOTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private GenericDAO<Location> locationDAO;

    @BeforeEach
    public void setUp() {
        super.setUp();
        locationDAO = new GenericDAO<>(em, Location.class);
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
    }

    @Test
    public void testInsert() {
        locationDAO.insert(location);
        customerDAO.insert(customer);

        Location foundLocation = em.find(Location.class, location.getId());
        assertNotNull(foundLocation);
        assertEquals(location.getStreet(), foundLocation.getStreet());
        assertEquals(location.getCivicNumber(), foundLocation.getCivicNumber());
        assertEquals(location.getCity(), foundLocation.getCity());
        assertEquals(location.getPostalCode(), foundLocation.getPostalCode());

        Customer foundCustomer = em.find(Customer.class, customer.getCustomerId());
        assertNotNull(foundCustomer);
        assertEquals(customer.getName(), foundCustomer.getName());
        assertEquals(customer.getSurname(), foundCustomer.getSurname());
    }

    @Test
    public void testUpdate() {
        locationDAO.insert(location);
        customerDAO.insert(customer);

        location.setStreet("Via Milano");
        location.setCivicNumber("20");
        locationDAO.update(location);

        customer.setName("Giovanni");
        customer.setSurname("Bianchi");
        customerDAO.update(customer);

        Location updatedLocation = locationDAO.findById(location.getId());
        assertEquals("Via Milano", updatedLocation.getStreet());
        assertEquals("20", updatedLocation.getCivicNumber());

        Customer updatedCustomer = customerDAO.findById(customer.getCustomerId());
        assertEquals("Giovanni", updatedCustomer.getName());
        assertEquals("Bianchi", updatedCustomer.getSurname());
    }

    @Test
    public void testDelete() {
        locationDAO.insert(location);
        customerDAO.insert(customer);
    
        int locationId = location.getId();
        int customerId = customer.getCustomerId();
    
        Location foundLocation = locationDAO.findById(locationId);
        assertNotNull(foundLocation);
        Customer foundCustomer = customerDAO.findById(customerId);
        assertNotNull(foundCustomer);
    
        customerDAO.delete(customer);
        locationDAO.delete(location);

    
        Location deletedLocation = locationDAO.findById(locationId);
        assertNull(deletedLocation);
    
        Customer deletedCustomer = customerDAO.findById(customerId);
        assertNull(deletedCustomer);
    }

    @Test
    public void testFindById() {
        locationDAO.insert(location);
        customerDAO.insert(customer);

        Location foundLocation = locationDAO.findById(location.getId());
        assertNotNull(foundLocation);
        assertEquals(location.getId(), foundLocation.getId());

        Customer foundCustomer = customerDAO.findById(customer.getCustomerId());
        assertNotNull(foundCustomer);
        assertEquals(customer.getCustomerId(), foundCustomer.getCustomerId());
    }

    @Test
    public void testFindAll() {
        Location location2 = new Location("Via Milano", "20", "Milano", "20100");
        Location location3 = new Location("Via Napoli", "30", "Napoli", "80100");
        Customer customer2 = new Customer("Luca", "Bianchi", location2, "luca.bianchi@example.com", "0987654321");
        Customer customer3 = new Customer("Anna", "Verdi", location3, "anna.verdi@example.com", "1122334455");


    
        locationDAO.insert(location);
        locationDAO.insert(location2);
        locationDAO.insert(location3);
        customerDAO.insert(customer);
        customerDAO.insert(customer2);
        customerDAO.insert(customer3);
    
        List<Location> locations = locationDAO.findAll();
        assertFalse(locations.isEmpty());
        assertTrue(locations.contains(location));
        assertTrue(locations.contains(location2));
        assertTrue(locations.contains(location3));
    
        List<Customer> customers = customerDAO.findAll();
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(customer));
        assertTrue(customers.contains(customer2));
        assertTrue(customers.contains(customer3));
    }
}
