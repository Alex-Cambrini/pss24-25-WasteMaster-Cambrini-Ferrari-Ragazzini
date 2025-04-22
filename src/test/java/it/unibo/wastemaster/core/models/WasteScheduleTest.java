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
	public void testToString() {
		String str = schedule.toString();
		assertTrue(str.contains("dayOfWeek=5"));
		assertTrue(str.contains("PLASTIC"));
	}

}
