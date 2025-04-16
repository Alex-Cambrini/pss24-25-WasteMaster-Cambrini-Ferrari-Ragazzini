package it.unibo.wastemaster.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AbstractDatabaseTest {
    protected EntityManagerFactory emf;
	protected EntityManager em;

	protected Location location;
	protected Customer customer;

	@BeforeEach
	public void setUp() {
		emf = Persistence.createEntityManagerFactory("test-pu");
		em = emf.createEntityManager();
		em.getTransaction().begin();

		location = new Location("Via Roma", "10", "Bologna", "40100");
		customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
		em.persist(customer);

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
