package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;

import org.junit.jupiter.api.BeforeEach;



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

}
