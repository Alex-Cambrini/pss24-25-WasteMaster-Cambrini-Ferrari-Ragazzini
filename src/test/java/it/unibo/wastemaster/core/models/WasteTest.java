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
	public void testGettersAndSetters() {
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
	public void testToString() {
		String expected = "Waste Type: plastic\n" +
				"Recyclable: Yes\n" +
				"Dangerous: No";
		assertEquals(expected, waste.toString());
	}

	@Test
	void testPersistence() {
		wasteDAO.insert(waste);
		int id = waste.getWasteId();
		Waste found = wasteDAO.findById(id);
		assertNotNull(found);
		assertEquals(waste.getWasteName(), found.getWasteName());
		assertEquals(waste.getIsRecyclable(), found.getIsRecyclable());
		assertEquals(waste.getIsDangerous(), found.getIsDangerous());
	}

	@Test
	void testSoftDelete() {
		wasteDAO.insert(waste);
		waste.delete();
		wasteDAO.update(waste);
		Waste found = wasteDAO.findById(waste.getWasteId());
		assertTrue(found.isDeleted());
	}

	@Test
	void testValidation() {
		Waste invalidWaste = new Waste(null, null, null);
		Set<ConstraintViolation<Waste>> violations = ValidateUtils.VALIDATOR.validate(invalidWaste);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Waste type must not be null")));
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("isRecyclable must not be null")));
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("isDangerous must not be null")));
	}
}
