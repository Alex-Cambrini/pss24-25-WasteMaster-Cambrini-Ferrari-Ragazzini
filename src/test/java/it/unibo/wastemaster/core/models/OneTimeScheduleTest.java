package it.unibo.wastemaster.core.models;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

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
		schedule = new OneTimeSchedule(customer, Waste.WasteType.ORGANIC, pickupDate);
	}

	@Test
	public void testGetterAndSetter() {
		assertEquals(pickupDate, schedule.getPickupDate());

		LocalDate newDate = pickupDate.plusDays(1);
		schedule.setPickupDate(newDate);
		assertEquals(newDate, schedule.getPickupDate());
	}

	@Test
	public void testInvalidSchedule() {
		OneTimeSchedule invalid = new OneTimeSchedule();
		invalid.setPickupDate(DateUtils.getCurrentDate().minusDays(2));

		Set<ConstraintViolation<OneTimeSchedule>> violations = ValidateUtils.VALIDATOR.validate(invalid);
		assertFalse(violations.isEmpty());

		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("customer")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("wasteType")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("pickupDate")));
	}

	@Test
	public void testValidSchedule() {
		Set<ConstraintViolation<OneTimeSchedule>> violations = ValidateUtils.VALIDATOR.validate(schedule);
		assertTrue(violations.isEmpty(), "Expected no validation errors for a valid OneTimeSchedule");
	}

	@Test
	public void testPersistence() {
		em.getTransaction().begin();
		em.persist(customer.getAddress());
		em.persist(customer);
		em.persist(schedule);
		em.getTransaction().commit();

		OneTimeSchedule found = em.find(OneTimeSchedule.class, schedule.getScheduleId());
		assertNotNull(found);
		assertEquals(pickupDate, found.getPickupDate());
		assertEquals(customer.getEmail(), found.getCustomer().getEmail());

		em.getTransaction().begin();
		em.remove(found);
		em.getTransaction().commit();

		OneTimeSchedule deleted = em.find(OneTimeSchedule.class, schedule.getScheduleId());
		assertNull(deleted);
	}
}