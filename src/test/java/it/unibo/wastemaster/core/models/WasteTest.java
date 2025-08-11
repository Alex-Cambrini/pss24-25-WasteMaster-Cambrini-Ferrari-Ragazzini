package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Waste;
import jakarta.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteTest extends AbstractDatabaseTest {

    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        waste = new Waste("plastic", true, false);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("plastic", waste.getWasteName());
        assertTrue(waste.getIsRecyclable());
        assertFalse(waste.getIsDangerous());

        waste.setWasteName("glass");
        waste.setIsRecyclable(false);
        waste.setIsDangerous(true);

        assertEquals("glass", waste.getWasteName());
        assertFalse(waste.getIsRecyclable());
        assertTrue(waste.getIsDangerous());
    }

    @Test
    void testToString() {
        String expected = """
                Waste Name: plastic
                Recyclable: Yes
                Dangerous: No""";
        assertEquals(expected, waste.toString());
    }

    @Test
    void testPersistence() {
        getWasteDAO().insert(waste);
        int id = waste.getWasteId();

        Optional<Waste> found = getWasteDAO().findById(id);
        assertTrue(found.isPresent());
        Waste foundWaste = found.get();

        assertEquals(waste.getWasteName(), foundWaste.getWasteName());
        assertEquals(waste.getIsRecyclable(), foundWaste.getIsRecyclable());
        assertEquals(waste.getIsDangerous(), foundWaste.getIsDangerous());
    }

    @Test
    void testSoftDelete() {
        getWasteDAO().insert(waste);
        waste.delete();
        getWasteDAO().update(waste);

        Optional<Waste> found = getWasteDAO().findById(waste.getWasteId());
        assertTrue(found.isPresent());
        Waste foundWaste = found.get();

        assertTrue(foundWaste.isDeleted());
    }

    @Test
    void testValidation() {
        Waste invalidWaste = new Waste(null, null, null);
        Set<ConstraintViolation<Waste>> violations =
                ValidateUtils.VALIDATOR.validate(invalidWaste);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("Waste type must not be null")));
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("isRecyclable must not be null")));
        assertTrue(violations.stream().anyMatch(
                v -> v.getMessage().contains("isDangerous must not be null")));
    }
}
