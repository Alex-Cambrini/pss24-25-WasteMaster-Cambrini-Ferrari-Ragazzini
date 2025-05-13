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
		waste = new Waste("plastic", true, false);
	}

	@Test
	public void testWasteGettersAndSetters() {
		assertEquals(waste.getWasteName(), "plastic");
		assertTrue(waste.getIsRecyclable());
		assertFalse(waste.getIsDangerous());

		waste.setWasteName("glass");
		waste.setIsRecyclable(false);
		waste.setIsDangerous(true);

		assertEquals(waste.getWasteName(), "glass");
		assertFalse(waste.getIsRecyclable());
		assertTrue(waste.getIsDangerous());
	}

	@Test
	public void testToString() {
    String expected = "Waste Type: " + waste.getWasteName() + "\n" +
                      "Recyclable: " + (waste.getIsRecyclable() ? "Yes" : "No") + "\n" +
                      "Dangerous: " + (waste.getIsDangerous() ? "Yes" : "No");
    assertEquals(expected, waste.toString());
	}

	@Test
	void testPersistence() {
		wasteDAO.insert(waste);
		int wasteId = waste.getWasteId();
		Waste found = wasteDAO.findById(wasteId);
		assertNotNull(found);
		assertEquals(waste.getWasteName(), found.getWasteName());
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
