package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WasteScheduleTest {

	private Waste waste;
	private WasteSchedule schedule;

	@BeforeEach
	public void setUp() {
		waste = new Waste(Waste.WasteType.PLASTIC, true, false);
		schedule = new WasteSchedule(waste, 5);
	}

	@Test
	public void testGetAndSetWaste() {
		assertEquals(waste, schedule.getWaste());
		Waste newWaste = new Waste(Waste.WasteType.GLASS, false, false);
		schedule.setWaste(newWaste);
		assertEquals(newWaste, schedule.getWaste());
	}

	@Test
	public void testGetAndSetDayOfWeek() {
		assertEquals(5, schedule.getDayOfWeek());
		schedule.setDayOfWeek(7);
		assertEquals(7, schedule.getDayOfWeek());
	}

	@Test
	public void testToString() {
		String str = schedule.toString();
		assertTrue(str.contains("dayOfWeek=5"));
		assertTrue(str.contains("PLASTIC"));
	}

	@Test
	public void testConstructorRejectsInvalidArguments() {
		IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () ->
			new WasteSchedule(null, 3)
		);
		assertEquals("Waste must not be null", e1.getMessage());

		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () ->
			new WasteSchedule(waste, 0)
		);
		assertEquals("dayOfWeek must be between 1 (Sunday) and 7 (Saturday)", e2.getMessage());

		IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () ->
			new WasteSchedule(waste, 8)
		);
		assertEquals("dayOfWeek must be between 1 (Sunday) and 7 (Saturday)", e3.getMessage());
	}

	@Test
	public void testSettersRejectInvalidArguments() {
		assertThrows(IllegalArgumentException.class, () -> schedule.setWaste(null));

		assertThrows(IllegalArgumentException.class, () -> schedule.setDayOfWeek(0));
		assertThrows(IllegalArgumentException.class, () -> schedule.setDayOfWeek(9));
	}
}
