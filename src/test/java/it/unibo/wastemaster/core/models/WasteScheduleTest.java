package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;

class WasteScheduleTest extends AbstractDatabaseTest {

	private Waste waste;
	private WasteSchedule schedule;

	@BeforeEach
	public void setUp() {
		super.setUp();
		waste = new Waste(Waste.WasteType.PLASTIC, true, false);
		schedule = new WasteSchedule(waste, DayOfWeek.FRIDAY);
	}

	@Test
	public void testGetterAndSetter() {
		assertEquals(waste, schedule.getWaste());
		assertEquals(DayOfWeek.FRIDAY, schedule.getDayOfWeek());

		Waste newWaste = new Waste(Waste.WasteType.GLASS, false, false);
		schedule.setWaste(newWaste);
		assertEquals(newWaste, schedule.getWaste());

		schedule.setDayOfWeek(DayOfWeek.SUNDAY);
		assertEquals(DayOfWeek.SUNDAY, schedule.getDayOfWeek());
	}

	@Test
	public void testToString() {
		String str = schedule.toString();
		assertTrue(str.contains("dayOfWeek=5"));
		assertTrue(str.contains("PLASTIC"));
	}

}
