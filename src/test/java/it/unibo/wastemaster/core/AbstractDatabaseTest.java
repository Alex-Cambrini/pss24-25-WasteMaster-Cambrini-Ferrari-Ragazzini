package it.unibo.wastemaster.core;

import java.util.Calendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
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


	@BeforeEach
	public void setUp() {
		emf = Persistence.createEntityManagerFactory("test-pu");
		em = emf.createEntityManager();
		em.getTransaction().begin();

		location = new Location("Via Roma", "10", "Bologna", "40100");
		customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        waste = new Waste(Waste.WasteType.PLASTIC, true, false);
        int scheduledDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        wasteSchedule = new WasteSchedule(waste, scheduledDay);
		em.persist(location);
        em.persist(customer);
        em.persist(waste);
        em.persist(wasteSchedule);

		em.getTransaction().commit();
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
