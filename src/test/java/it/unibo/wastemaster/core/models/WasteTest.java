package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WasteTest {

	private Waste waste;

	@BeforeEach
	public void setUp() {
		waste = new Waste(Waste.WasteType.PLASTIC, true, false);
	}

	@Test
	public void testGetAndSetType() {
		assertEquals(Waste.WasteType.PLASTIC, waste.getType());
		waste.setType(Waste.WasteType.GLASS);
		assertEquals(Waste.WasteType.GLASS, waste.getType());
	}

	@Test
	public void testGetAndSetIsRecyclable() {
		assertTrue(waste.getIsRecyclable());
		waste.setIsRecyclable(false);
		assertFalse(waste.getIsRecyclable());
	}

	@Test
	public void testGetAndSetIsDangerous() {
		assertFalse(waste.getIsDangerous());
		waste.setIsDangerous(true);
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
	public void testConstructorRejectsNullArguments() {
		IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () ->
			new Waste(null, true, false)
		);
		assertEquals("Waste type must not be null", e1.getMessage());

		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () ->
			new Waste(Waste.WasteType.PAPER, null, false)
		);
		assertEquals("isRecyclable must not be null", e2.getMessage());

		IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () ->
			new Waste(Waste.WasteType.PAPER, true, null)
		);
		assertEquals("isDangerous must not be null", e3.getMessage());
	}
}
