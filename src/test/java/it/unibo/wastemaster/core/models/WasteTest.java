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
	public void testGetType() {
		assertEquals(Waste.WasteType.PLASTIC, waste.getType());
	}

	@Test
	public void testSetType() {
		waste.setType(Waste.WasteType.GLASS);
		assertEquals(Waste.WasteType.GLASS, waste.getType());
	}

	@Test
	public void testIsRecyclable() {
		assertTrue(waste.getIsRecyclable());
	}

	@Test
	public void testSetIsRecyclable() {
		waste.setIsRecyclable(false);
		assertFalse(waste.getIsRecyclable());
	}

	@Test
	public void testIsDangerous() {
		assertFalse(waste.getIsDangerous());
	}

	@Test
	public void testSetIsDangerous() {
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
}