package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;

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
		waste = new Waste("plastic", true, false);

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
			oneTimeScheduleManager.createOneTimeSchedule(customer, waste, invalidDate);
		});

		OneTimeSchedule newSchedule = oneTimeScheduleManager.createOneTimeSchedule(customer, waste,
				validDate);
		assertNotNull(newSchedule);
		assertEquals(newSchedule.getScheduleStatus(), ScheduleStatus.ACTIVE);

		Collection associatedCollection = collectionManager.getActiveCollectionByOneTimeSchedule(newSchedule);
		assertNotNull(associatedCollection);
		assertEquals(associatedCollection.getCollectionStatus(), Collection.CollectionStatus.PENDING);
	}

	@Test
	void testSoftDeleteOneTimeSchedule_allCases() {
		LocalDate validDate = dateUtils.getCurrentDate().plusDays(3);

		// 1) Null args
		assertThrows(IllegalArgumentException.class, () -> oneTimeScheduleManager.softDeleteOneTimeSchedule(null));

		// 2) Already CANCELLED → false
		OneTimeSchedule cancelledSchedule = new OneTimeSchedule(customer, waste, validDate);
		cancelledSchedule.setScheduleStatus(ScheduleStatus.CANCELLED);
		assertFalse(oneTimeScheduleManager.softDeleteOneTimeSchedule(cancelledSchedule));

		// 3) ACTIVE → CANCELLED
		OneTimeSchedule activeSchedule = oneTimeScheduleManager.createOneTimeSchedule(customer, waste, validDate);
		boolean deleted = oneTimeScheduleManager.softDeleteOneTimeSchedule(activeSchedule);
		assertTrue(deleted);

		// Check collection also cancelled
		Collection cancelledCollection = collectionManager.getCancelledCollectionsOneTimeSchedule(activeSchedule)
				.stream()
				.findFirst()
				.orElse(null);

		assertNotNull(cancelledCollection);
		assertEquals(Collection.CollectionStatus.CANCELLED, cancelledCollection.getCollectionStatus());

		// 4) Try to delete again → false
		assertFalse(oneTimeScheduleManager.softDeleteOneTimeSchedule(activeSchedule));
	}
}
