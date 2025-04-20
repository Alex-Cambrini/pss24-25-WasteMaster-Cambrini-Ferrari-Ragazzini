package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.utils.DateUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OneTimeScheduleTest {

	private OneTimeSchedule schedule;
	private Customer customer;
	private LocalDate pickupDate;

	@BeforeEach
	public void setUp() {
		Location location = new Location("Via Dante", "5", "Roma", "00100");
		customer = new Customer("Luca", "Verdi", location, "luca@example.com", "3456789012");
		pickupDate = DateUtils.getCurrentDate();
		schedule = new OneTimeSchedule(customer, Waste.WasteType.ORGANIC, Schedule.ScheduleStatus.ACTIVE, pickupDate);
	}

	@Test
	public void testGetAndSetPickupDate() {
		assertEquals(pickupDate, schedule.getPickupDate());

		LocalDate newDate = pickupDate.plusDays(1); 
		schedule.setPickupDate(newDate);
		assertEquals(newDate, schedule.getPickupDate());
	}

	@Test
	public void testConstructorRejectsNullPickupDate() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
			new OneTimeSchedule(customer, Waste.WasteType.GLASS, Schedule.ScheduleStatus.ACTIVE, null)
		);
		assertEquals("pickupDate must not be null", e.getMessage());
	}

	@Test
	public void testSetterRejectsNullPickupDate() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
			schedule.setPickupDate(null)
		);
		assertEquals("pickupDate must not be null", e.getMessage());
	}
}
