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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	public void testCreateOneTimeSchedule() {
		LocalDate invalidDate = dateUtils.getCurrentDate().plusDays(1);
		LocalDate validDate = dateUtils.getCurrentDate().plusDays(3);

		assertThrows(IllegalArgumentException.class, () -> {
			oneTimeScheduleManager.createOneTimeSchedule(customer, Waste.WasteType.ORGANIC, invalidDate);
		});

		OneTimeSchedule newSchedule = oneTimeScheduleManager.createOneTimeSchedule(customer, Waste.WasteType.ORGANIC,
				validDate);
		assertNotNull(newSchedule);
		assertEquals(newSchedule.getStatus(), ScheduleStatus.ACTIVE);

		Collection associatedCollection = collectionManager.getActiveCollectionByOneTimeSchedule(newSchedule);
		assertNotNull(associatedCollection);
		assertEquals(associatedCollection.getCollectionStatus(), Collection.CollectionStatus.PENDING);
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
	void testUpdateStatusOneTimeSchedule_allCases() {
		LocalDate validDate = dateUtils.getCurrentDate().plusDays(3);

		// 1) Null args
		assertThrows(IllegalArgumentException.class, () ->
			oneTimeScheduleManager.updateStatusOneTimeSchedule(null, ScheduleStatus.ACTIVE)
		);

		OneTimeSchedule s0 = new OneTimeSchedule(customer, Waste.WasteType.GLASS, validDate);
		ValidateUtils.validateEntity(s0);
		assertThrows(IllegalArgumentException.class, () ->
			oneTimeScheduleManager.updateStatusOneTimeSchedule(s0, null)
		);
	
		// 2) Already CANCELLED → false
		OneTimeSchedule s2 = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, validDate);
		ValidateUtils.validateEntity(s2);
		s2.setStatus(ScheduleStatus.CANCELLED);
		assertFalse(oneTimeScheduleManager.updateStatusOneTimeSchedule(s2, ScheduleStatus.ACTIVE));
			
		// 3) ACTIVE → PAUSED
		OneTimeSchedule s3 = oneTimeScheduleManager.createOneTimeSchedule(customer, Waste.WasteType.ORGANIC, validDate);
		assertTrue(oneTimeScheduleManager.updateStatusOneTimeSchedule(s3, ScheduleStatus.PAUSED));

		Collection c3 = collectionManager.getCancelledCollectionsOneTimeSchedule(s3).stream().findFirst().orElse(null);		
		assertNotNull(c3);
		assertEquals(c3.getCollectionStatus(), Collection.CollectionStatus.CANCELLED);

		// 3) PAUSED → ACTIVE
		assertTrue(oneTimeScheduleManager.updateStatusOneTimeSchedule(s3, ScheduleStatus.ACTIVE));
		c3 = collectionManager.getActiveCollectionByOneTimeSchedule(s3);
		assertNotNull(c3);
	}

	@Test
	void testCancelFail() {
		LocalDate date = dateUtils.getCurrentDate().plusDays(1);

		OneTimeSchedule schedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, date);
		ValidateUtils.validateEntity(schedule);
		oneTimeScheduleDAO.insert(schedule);
		collectionManager.generateOneTimeCollection(schedule);

		boolean result = oneTimeScheduleManager.updateStatusOneTimeSchedule(schedule, ScheduleStatus.CANCELLED);
		assertFalse(result);
	}
}
