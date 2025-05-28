package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationTest extends AbstractDatabaseTest {

    private Location location;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Milano", "20100");
    }

    @Test
    void testGetterSetter() {
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
    void testToString() {
        String expectedString = "Via Roma 10, Milano, 20100";
        assertEquals(expectedString, location.toString());
    }

    @Test
    void testInvalidLocation() {
        Location invalid = new Location("", "", "", "");

        Set<ConstraintViolation<Location>> violations =
                ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("street")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("civicNumber")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("city")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("postalCode")));
    }

    @Test
    void testValidLocation() {
        Location valid = new Location("Via Garibaldi", "15", "Torino", "10100");

        Set<ConstraintViolation<Location>> violations =
                ValidateUtils.VALIDATOR.validate(valid);
        assertTrue(violations.isEmpty(),
                "Expected no validation errors for a valid Location");
    }

    @Test
    void testPersistence() {
        getLocationDAO().insert(location);

        int locationId = location.getId();
        Location found = getLocationDAO().findById(locationId);
        assertNotNull(found);
        assertEquals(location.getStreet(), found.getStreet());
        assertEquals(location.getCivicNumber(), found.getCivicNumber());
        assertEquals(location.getCity(), found.getCity());
        assertEquals(location.getPostalCode(), found.getPostalCode());

        getLocationDAO().delete(found);
        Location deleted = getLocationDAO().findById(locationId);
        assertNull(deleted);
    }
}
