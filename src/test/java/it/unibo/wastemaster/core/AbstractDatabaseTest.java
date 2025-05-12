package it.unibo.wastemaster.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.dao.EmployeeDAO;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Trip;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.services.CollectionManager;
import it.unibo.wastemaster.core.services.CustomerManager;
import it.unibo.wastemaster.core.services.EmployeeManager;
import it.unibo.wastemaster.core.services.OneTimeScheduleManager;
import it.unibo.wastemaster.core.services.RecurringScheduleManager;
import it.unibo.wastemaster.core.services.TripManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import it.unibo.wastemaster.core.utils.DateUtils;
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
    protected EmployeeDAO employeeDAO;
    protected OneTimeScheduleDAO oneTimeScheduleDAO;
    protected RecurringScheduleDAO recurringScheduleDAO;
    protected WasteScheduleDAO wasteScheduleDAO;
    protected VehicleDAO vehicleDAO;
    protected TripDAO tripDAO;


    protected CustomerManager customerManager;
    protected EmployeeManager employeeManager;
    protected WasteScheduleManager wasteScheduleManager;
    protected CollectionManager collectionManager;
    protected RecurringScheduleManager recurringScheduleManager;
    protected OneTimeScheduleManager oneTimeScheduleManager;
    protected TripManager tripManager;
    protected DateUtils dateUtils;

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
    dateUtils = new DateUtils();

    wasteDAO              = new GenericDAO<>(em, Waste.class);
    locationDAO           = new GenericDAO<>(em, Location.class);
    customerDAO           = new CustomerDAO(em);
    employeeDAO           = new EmployeeDAO(em);
    wasteScheduleDAO      = new WasteScheduleDAO(em);
    oneTimeScheduleDAO    = new OneTimeScheduleDAO(em);
    recurringScheduleDAO  = new RecurringScheduleDAO(em);
    collectionDAO         = new CollectionDAO(em);
    vehicleDAO            = new VehicleDAO(em);
    tripDAO               = new TripDAO(em);
    

    customerManager       = new CustomerManager(customerDAO);
    employeeManager       = new EmployeeManager(employeeDAO);
    wasteScheduleManager  = new WasteScheduleManager(wasteScheduleDAO);

    recurringScheduleManager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);
    collectionManager        = new CollectionManager(collectionDAO, recurringScheduleManager);
    recurringScheduleManager.setCollectionManager(collectionManager);
    oneTimeScheduleManager = new OneTimeScheduleManager(oneTimeScheduleDAO, collectionManager);
    tripManager = new TripManager(tripDAO);
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