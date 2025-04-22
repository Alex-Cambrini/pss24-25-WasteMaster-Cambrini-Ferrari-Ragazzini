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
		em.getTransaction().begin();
		em.persist(waste);
		em.persist(schedule);
		em.getTransaction().commit();

		WasteSchedule found = em.find(WasteSchedule.class, schedule.getScheduleId());
		assertNotNull(found);
		assertEquals(waste.getType(), found.getWaste().getType());
		assertEquals(schedule.getDayOfWeek(), found.getDayOfWeek());

		em.getTransaction().begin();
		em.remove(found);
		em.getTransaction().commit();

		WasteSchedule deleted = em.find(WasteSchedule.class, schedule.getScheduleId());
		assertNull(deleted);
	}

	@Test
	public void testToString() {
		String result = schedule.toString();
		assertNotNull(result);
		assertTrue(result.contains("WasteSchedule{"));
		assertTrue(result.contains("dayOfWeek=" + schedule.getDayOfWeek().name()));
		assertTrue(result.contains("PLASTIC"));
	}
}
