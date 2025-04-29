package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.ValidateUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OneTimeScheduleManagerTest extends AbstractDatabaseTest {

	private OneTimeScheduleManager oneTimeScheduleManager;
	private Customer customer;
	private Location location;
	private Waste waste;

	@BeforeEach
	public void setUp() {
		super.setUp();
		em.getTransaction().begin();

		location = new Location("Via Milano", "22", "Modena", "41100");
		customer = new Customer("Luca", "Verdi", location, "luca.verdi@example.com", "3334567890");
		waste = new Waste(Waste.WasteType.PLASTIC, true, false);

		locationDAO.insert(location);
		customerDAO.insert(customer);
		wasteDAO.insert(waste);

		oneTimeScheduleManager = new OneTimeScheduleManager(oneTimeScheduleDAO, collectionManager);
	}

	@Test
	void testCreateOneTimeSchedule() {
		LocalDate pickupDate = dateUtils.getCurrentDate().plusDays(5);

		oneTimeScheduleManager.createOneTimeSchedule(customer, Waste.WasteType.PLASTIC, ScheduleStatus.SCHEDULED,
				pickupDate);

		OneTimeSchedule schedule = oneTimeScheduleDAO.findAll().get(0);
		assertEquals(Waste.WasteType.PLASTIC, schedule.getWasteType());
		assertEquals(pickupDate, schedule.getPickupDate());

		Collection collection = collectionDAO.findAll().get(0);
		assertEquals(pickupDate, collection.getCollectionDate());
		assertEquals(Waste.WasteType.PLASTIC, collection.getWaste());
		assertEquals(customer, collection.getCustomer());
	}

	@Test
	void testUpdateDateOneTimeSchedule() {
		LocalDate oldDate = dateUtils.getCurrentDate().plusDays(5);
		LocalDate newDate = oldDate.plusDays(3);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, oldDate);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.updateDateOneTimeSchedule(schedule, newDate);
		assertTrue(result);

		Collection updated = collectionDAO.findAll().get(0);
		assertEquals(newDate, updated.getCollectionDate());
		assertEquals(newDate, schedule.getPickupDate());
	}

	@Test
	void testUpdateWasteTypeOneTimeSchedule() {
		LocalDate date = dateUtils.getCurrentDate().plusDays(5);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, date);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.updateWasteTypeOneTimeSchedule(schedule, Waste.WasteType.GLASS);
		assertTrue(result);

		Collection updated = collectionDAO.findAll().get(0);
		assertEquals(Waste.WasteType.GLASS, updated.getWaste());
		assertEquals(Waste.WasteType.GLASS, schedule.getWasteType());
	}

	@Test
	void testUpdateDateFail() {
		LocalDate oldDate = dateUtils.getCurrentDate().plusDays(1);
		LocalDate newDate = oldDate.plusDays(3);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, oldDate);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.updateDateOneTimeSchedule(schedule, newDate);
		assertFalse(result);
	}

	@Test
	void testCancelOneTimeSchedule() {
		LocalDate date = dateUtils.getCurrentDate().plusDays(5);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, date);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.cancelOneTimeSchedule(schedule);
		assertTrue(result);

		Collection updated = collectionDAO.findAll().get(0);
		assertEquals(Collection.CollectionStatus.CANCELLED, updated.getCollectionStatus());
		assertEquals(ScheduleStatus.CANCELLED, schedule.getStatus());
	}

	@Test
	void testCancelFail() {
		LocalDate date = dateUtils.getCurrentDate().plusDays(1);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, date);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.cancelOneTimeSchedule(schedule);
		assertFalse(result);
	}
}
