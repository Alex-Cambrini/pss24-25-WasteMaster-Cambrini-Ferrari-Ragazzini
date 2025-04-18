package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    private Location location;

    @BeforeEach
    public void setUp() {
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
    public void testLocationValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Location(null, "10", "Milano", "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("", "10", "Milano", "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", null, "Milano", "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", "", "Milano", "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", "10", null, "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", "10", "", "20100");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", "10", "Milano", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Location("Via Roma", "10", "Milano", "");
        });
}
}
