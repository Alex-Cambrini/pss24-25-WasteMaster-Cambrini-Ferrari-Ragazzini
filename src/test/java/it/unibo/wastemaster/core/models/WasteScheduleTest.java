package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.util.Set;

class WasteScheduleTest extends AbstractDatabaseTest {

	private Waste plastic;
	private WasteSchedule schedule;

	@BeforeEach
	public void setUp() {
		super.setUp();
		plastic = new Waste("plastic", true, false);
		schedule = new WasteSchedule(plastic, DayOfWeek.FRIDAY);
	}

	@Test
	public void testGetterAndSetter() {
		assertEquals(plastic, schedule.getWaste());
		assertEquals(DayOfWeek.FRIDAY, schedule.getDayOfWeek());

		Waste glass = new Waste("glass", false, false);
		schedule.setWaste(glass);
		assertEquals(glass, schedule.getWaste());

		schedule.setDayOfWeek(DayOfWeek.SUNDAY);
		assertEquals(DayOfWeek.SUNDAY, schedule.getDayOfWeek());
	}

	@Test
	public void testInvalidWasteSchedule() {
		WasteSchedule invalid = new WasteSchedule();
		Set<ConstraintViolation<WasteSchedule>> violations = ValidateUtils.VALIDATOR.validate(invalid);

		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("waste")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dayOfWeek")));
	}

	@Test
	public void testValidWasteSchedule() {
		Set<ConstraintViolation<WasteSchedule>> violations = ValidateUtils.VALIDATOR.validate(schedule);
		assertTrue(violations.isEmpty(), "Expected no validation errors for a valid WasteSchedule");
	}

	@Test
	public void testPersistence() {
		wasteDAO.insert(plastic);
		wasteScheduleDAO.insert(schedule);
		int ScheduleId = schedule.getScheduleId();
		WasteSchedule found = wasteScheduleDAO.findById(ScheduleId);
		assertNotNull(found);
		assertEquals(plastic, found.getWaste());
		assertEquals(schedule.getDayOfWeek(), found.getDayOfWeek());

		wasteScheduleDAO.delete(found);
		WasteSchedule deleted = wasteScheduleDAO.findById(ScheduleId);
		assertNull(deleted);
	}
}
