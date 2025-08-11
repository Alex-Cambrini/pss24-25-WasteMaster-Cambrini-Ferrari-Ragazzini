package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenericDAOTest extends AbstractDatabaseTest {

    private Location location1;
    private Location location2;
    private Location location3;
    private Customer customer1;
    private Customer customer2;
    private Customer customer3;

    @BeforeEach
    public void setUp() {
        super.setUp();

        location1 = new Location("Via Roma", "10", "Bologna", "40100");
        location2 = new Location("Via Milano", "20", "Milano", "20100");
        location3 = new Location("Via Napoli", "30", "Napoli", "80100");

        customer1 = new Customer("Mario", "Rossi", location1, "mario.rossi@example.com",
                "1234567890");
        customer2 = new Customer("Luca", "Bianchi", location2, "luca.bianchi@example.com",
                "0987654321");
        customer3 = new Customer("Anna", "Verdi", location3, "anna.verdi@example.com",
                "1122334455");
    }

    @Test
    public void testInsert() {
        getLocationDAO().insert(location1);
        getCustomerDAO().insert(customer1);

        Optional<Location> foundLocation = getLocationDAO().findById(location1.getId());
        assertTrue(foundLocation.isPresent());
        Location location = foundLocation.get();
        assertEquals(location1.getStreet(), location.getStreet());
        assertEquals(location1.getCivicNumber(), location.getCivicNumber());
        assertEquals(location1.getCity(), location.getCity());
        assertEquals(location1.getPostalCode(), location.getPostalCode());

        Optional<Customer> foundCustomer =
                getCustomerDAO().findById(customer1.getCustomerId());
        assertTrue(foundCustomer.isPresent());
        Customer customer = foundCustomer.get();
        assertEquals(customer1.getName(), customer.getName());
        assertEquals(customer1.getSurname(), customer.getSurname());
    }

    @Test
    public void testUpdate() {
        getLocationDAO().insert(location1);
        getCustomerDAO().insert(customer1);

        location1.setStreet("Via Torino");
        location1.setCivicNumber("99");
        getLocationDAO().update(location1);

        final String updatedSurname = "Bianchi";
        customer1.setName("Giovanni");
        customer1.setSurname(updatedSurname);
        getCustomerDAO().update(customer1);

        Optional<Location> updatedLocationOpt =
                getLocationDAO().findById(location1.getId());
        assertTrue(updatedLocationOpt.isPresent());
        Location updatedLocation = updatedLocationOpt.get();
        assertEquals("Via Torino", updatedLocation.getStreet());
        assertEquals("99", updatedLocation.getCivicNumber());

        Optional<Customer> updatedCustomerOpt =
                getCustomerDAO().findById(customer1.getCustomerId());
        assertTrue(updatedCustomerOpt.isPresent());
        Customer updatedCustomer = updatedCustomerOpt.get();
        assertEquals("Giovanni", updatedCustomer.getName());
        assertEquals(updatedSurname, updatedCustomer.getSurname());
    }

    @Test
    public void testDelete() {
        getLocationDAO().insert(location1);
        getCustomerDAO().insert(customer1);

        int locationId = location1.getId();
        int customerId = customer1.getCustomerId();

        assertNotNull(getLocationDAO().findById(locationId));
        assertNotNull(getCustomerDAO().findById(customerId));

        getCustomerDAO().delete(customer1);
        getLocationDAO().delete(location1);

        assertNull(getLocationDAO().findById(locationId));
        assertNull(getCustomerDAO().findById(customerId));
    }

    @Test
    public void testFindById() {
        getLocationDAO().insert(location1);
        getCustomerDAO().insert(customer1);

        Optional<Location> foundLocation = getLocationDAO().findById(location1.getId());
        assertTrue(foundLocation.isPresent());
        assertEquals(location1.getId(), foundLocation.get().getId());

        Optional<Customer> foundCustomer =
                getCustomerDAO().findById(customer1.getCustomerId());
        assertTrue(foundCustomer.isPresent());
        assertEquals(customer1.getCustomerId(), foundCustomer.get().getCustomerId());
    }

    @Test
    public void testFindAll() {
        getLocationDAO().insert(location1);
        getLocationDAO().insert(location2);
        getLocationDAO().insert(location3);
        getCustomerDAO().insert(customer1);
        getCustomerDAO().insert(customer2);
        getCustomerDAO().insert(customer3);

        List<Location> locations = getLocationDAO().findAll();
        assertFalse(locations.isEmpty());
        assertTrue(locations.contains(location1));
        assertTrue(locations.contains(location2));
        assertTrue(locations.contains(location3));

        List<Customer> customers = getCustomerDAO().findAll();
        assertFalse(customers.isEmpty());
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
        assertTrue(customers.contains(customer3));
    }
}
