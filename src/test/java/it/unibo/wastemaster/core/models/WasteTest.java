package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

class WasteTest extends AbstractDatabaseTest {

	private Waste waste;

	@BeforeEach
	public void setUp() {
		super.setUp();
		waste = new Waste(Waste.WasteType.PLASTIC, true, false);
	}

	@Test
	public void testWasteGettersAndSetters() {
		assertEquals(Waste.WasteType.PLASTIC, waste.getType());
		assertTrue(waste.getIsRecyclable());
		assertFalse(waste.getIsDangerous());

		waste.setType(Waste.WasteType.GLASS);
		waste.setIsRecyclable(false);
		waste.setIsDangerous(true);

		assertEquals(Waste.WasteType.GLASS, waste.getType());
		assertFalse(waste.getIsRecyclable());
		assertTrue(waste.getIsDangerous());
	}

	@Test
	public void testToString() {
		String str = waste.toString();
		assertTrue(str.contains("PLASTIC"));
		assertTrue(str.contains("true"));
		assertTrue(str.contains("false"));
	}

	    @Test
    void testPersistence() {
        wasteDAO.insert(waste);
		int wasteId = waste.getWasteId();
        Waste found = wasteDAO.findById(wasteId);
        assertNotNull(found);
        assertEquals(waste.getType(), found.getType());
        assertEquals(waste.getIsRecyclable(), found.getIsRecyclable());
        assertEquals(waste.getIsDangerous(), found.getIsDangerous());

		wasteDAO.delete(found);
        Waste deleted = wasteDAO.findById(wasteId);
        assertNull(deleted);
	}

	@Test
    void testWasteValidation() {
        Waste invalidWaste = new Waste(null, null, null);

        Set<ConstraintViolation<Waste>> violations = ValidateUtils.VALIDATOR.validate(invalidWaste);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Waste type must not be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("isRecyclable must not be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("isDangerous must not be null")));
    }
}
