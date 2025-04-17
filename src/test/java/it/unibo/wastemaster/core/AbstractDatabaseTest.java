package it.unibo.wastemaster.core;

import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.services.CollectionManager;
import it.unibo.wastemaster.core.services.CustomerManager;
import it.unibo.wastemaster.core.services.RecurringScheduleManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AbstractDatabaseTest {
    protected EntityManagerFactory emf;
	protected EntityManager em;

	protected Location location;
	protected Customer customer;
    protected Waste waste;
    protected WasteSchedule wasteSchedule;

    protected CollectionDAO collectionDAO;
    protected CustomerDAO customerDAO;
    protected OneTimeScheduleDAO oneTimeScheduleDAO;
    protected RecurringScheduleDAO recurringScheduleDAO;
    protected WasteScheduleDAO wasteScheduleDAO;
    protected CustomerManager customerManager;
    protected <T> GenericDAO<T> createGenericDAO(Class<T> clazz) {
        return new GenericDAO<>(em, clazz);
    }

    protected WasteScheduleManager wasteScheduleManager;
    protected CollectionManager collectionManager;
    protected RecurringScheduleManager recurringScheduleManager;

    



	@BeforeEach
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        em = emf.createEntityManager();
        em.getTransaction().begin();

        location = new Location("Via Roma", "10", "Bologna", "40100");
        em.persist(location);

        customerDAO = new CustomerDAO(em);
        customerManager = new CustomerManager(customerDAO);

        customer = customerManager.addCustomer("Mario", "Rossi", "mario.rossi@example.com",
            "1234567890", "Via Roma", "10", "Bologna", "40100");

        waste = new Waste(Waste.WasteType.PLASTIC, true, false);
        int scheduledDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        wasteSchedule = new WasteSchedule(waste, scheduledDay);

        em.persist(waste);
        em.persist(wasteSchedule);

        em.getTransaction().commit();

        collectionDAO = new CollectionDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);

        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
        collectionManager = new CollectionManager(collectionDAO, null);
        recurringScheduleManager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);

    }


    @AfterEach
	public void tearDown() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		if (em.isOpen()) {
			em.close();
		}
		if (emf.isOpen()) {
			emf.close();
		}
	}
}
