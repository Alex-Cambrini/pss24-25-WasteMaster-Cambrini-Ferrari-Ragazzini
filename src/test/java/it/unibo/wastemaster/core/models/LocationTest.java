package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest extends AbstractDatabaseTest {

    private Location location;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Milano", "20100");
    }

    @Test
    public void testGetterSetter() {
        location.setStreet("Via Milano");
        assertEquals("Via Milano", location.getStreet());

        location.setCivicNumber("20");
        assertEquals("20", location.getCivicNumber());

        location.setCity("Roma");
        assertEquals("Roma", location.getCity());

        location.setPostalCode("00100");
        assertEquals("00100", location.getPostalCode());
    }

    @Test
    public void testToString() {
        String expectedString = "Via Roma, 10, Milano, 20100";
        assertEquals(expectedString, location.toString());
    }

    @Test
    public void testInvalidLocation() {
        Location invalid = new Location("", "", "", "");

        Set<ConstraintViolation<Location>> violations = ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("street")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("civicNumber")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("city")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("postalCode")));
    }

    @Test
    public void testValidLocation() {
        Location valid = new Location("Via Garibaldi", "15", "Torino", "10100");

        Set<ConstraintViolation<Location>> violations = ValidateUtils.VALIDATOR.validate(valid);
        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid Location");
    }

    @Test
    public void testPersistence() {
        em.getTransaction().begin();
        em.persist(location);
        em.getTransaction().commit();

        Location found = em.find(Location.class, location.getId());
        assertNotNull(found);
        assertEquals(location.getStreet(), found.getStreet());
        assertEquals(location.getCivicNumber(), found.getCivicNumber());
        assertEquals(location.getCity(), found.getCity());
        assertEquals(location.getPostalCode(), found.getPostalCode());

        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();

        Location deleted = em.find(Location.class, location.getId());
        assertNull(deleted);
    }
}
