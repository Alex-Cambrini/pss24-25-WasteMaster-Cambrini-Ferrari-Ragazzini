package it.unibo.wastemaster.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.services.CollectionManager;
import it.unibo.wastemaster.core.services.CustomerManager;
import it.unibo.wastemaster.core.services.RecurringScheduleManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public abstract class AbstractDatabaseTest {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    protected GenericDAO<Location> locationDAO;
    protected GenericDAO<Waste> wasteDAO;
    protected CollectionDAO collectionDAO;
    protected CustomerDAO customerDAO;
    protected OneTimeScheduleDAO oneTimeScheduleDAO;
    protected RecurringScheduleDAO recurringScheduleDAO;
    protected WasteScheduleDAO wasteScheduleDAO;

    protected CustomerManager customerManager;
    protected WasteScheduleManager wasteScheduleManager;
    protected CollectionManager collectionManager;
    protected RecurringScheduleManager recurringScheduleManager;

    @BeforeAll
    public static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        if (emf == null) {
            System.out.println("Failed to create EntityManagerFactory.");
        }
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        wasteDAO = new GenericDAO<Waste>(em, Waste.class);
        locationDAO = new GenericDAO<Location>(em, Location.class);
        customerDAO = new CustomerDAO(em);
        customerManager = new CustomerManager(customerDAO);

        wasteScheduleDAO = new WasteScheduleDAO(em);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);

        recurringScheduleDAO = new RecurringScheduleDAO(em);
        recurringScheduleManager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);

        collectionDAO = new CollectionDAO(em);
        collectionManager = new CollectionManager(collectionDAO, recurringScheduleManager);

        recurringScheduleManager.setCollectionManager(collectionManager);

        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
    }

    @AfterEach
    public void tearDown() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @AfterAll
    public static void cleanUp() {
        if (emf != null) {
            emf.close();
        }
    }
}