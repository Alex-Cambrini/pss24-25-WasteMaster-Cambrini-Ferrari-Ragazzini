package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OneTimeScheduleTest {

	private OneTimeSchedule schedule;
	private Customer customer;
	private Date pickupDate;

	@BeforeEach
	public void setUp() {
		Location location = new Location("Via Dante", "5", "Roma", "00100");
		customer = new Customer("Luca", "Verdi", location, "luca@example.com", "3456789012");
		pickupDate = new Date();
		schedule = new OneTimeSchedule(customer, Waste.WasteType.ORGANIC, Schedule.ScheduleStatus.ACTIVE, pickupDate);
	}

	@Test
	public void testGetAndSetPickupDate() {
		assertEquals(pickupDate, schedule.getPickupDate());

		Date newDate = new Date(pickupDate.getTime() + 1000000);
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
