package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OneTimeScheduleTest extends AbstractDatabaseTest {

	private OneTimeSchedule schedule;
	private Customer customer;
	private LocalDate pickupDate;

	@BeforeEach
	public void setUp() {
		super.setUp();
		Location location = new Location("Via Dante", "5", "Roma", "00100");
		customer = new Customer("Luca", "Verdi", location, "luca@example.com", "3456789012");
		pickupDate = DateUtils.getCurrentDate();
		schedule = new OneTimeSchedule(customer, Waste.WasteType.ORGANIC, Schedule.ScheduleStatus.ACTIVE, pickupDate);
	}

	@Test
	public void testGetterSetter() {
		assertEquals(pickupDate, schedule.getPickupDate());

		LocalDate newDate = pickupDate.plusDays(1);
		schedule.setPickupDate(newDate);
		assertEquals(newDate, schedule.getPickupDate());
	}

	@Test
	public void testConstructorRejectsNullPickupDate() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> new OneTimeSchedule(customer, Waste.WasteType.GLASS, Schedule.ScheduleStatus.ACTIVE, null));
		assertEquals("pickupDate must not be null", ex.getMessage());
	}

	@Test
	public void testSetterRejectsNullPickupDate() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> schedule.setPickupDate(null));
		assertEquals("pickupDate must not be null", ex.getMessage());
	}

	@Test
	public void testPersistence() {
		em.getTransaction().begin();
		em.persist(customer.getAddress());
		em.persist(customer);
		em.persist(schedule);
		em.getTransaction().commit();

		OneTimeSchedule found = em.find(OneTimeSchedule.class, schedule.getId());
		assertNotNull(found);
		assertEquals(pickupDate, found.getPickupDate());
		assertEquals(customer.getEmail(), found.getCustomer().getEmail());

		em.getTransaction().begin();
		em.remove(found);
		em.getTransaction().commit();

		OneTimeSchedule deleted = em.find(OneTimeSchedule.class, schedule.getId());
		assertNull(deleted);
	}
}