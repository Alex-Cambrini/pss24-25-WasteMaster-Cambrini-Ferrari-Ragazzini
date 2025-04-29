package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	
}
