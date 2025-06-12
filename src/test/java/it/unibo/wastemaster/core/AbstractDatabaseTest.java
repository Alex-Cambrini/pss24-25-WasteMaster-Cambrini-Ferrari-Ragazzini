package it.unibo.wastemaster.core;

import it.unibo.wastemaster.core.dao.AccountDAO;
import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.dao.EmployeeDAO;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.TripDAO;
import it.unibo.wastemaster.core.dao.InvoiceDAO;
import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.dao.WasteDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.services.AccountManager;
import it.unibo.wastemaster.core.services.CollectionManager;
import it.unibo.wastemaster.core.services.CustomerManager;
import it.unibo.wastemaster.core.services.EmployeeManager;
import it.unibo.wastemaster.core.services.OneTimeScheduleManager;
import it.unibo.wastemaster.core.services.RecurringScheduleManager;
import it.unibo.wastemaster.core.services.TripManager;
import it.unibo.wastemaster.core.services.VehicleManager;
import it.unibo.wastemaster.core.services.WasteManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import it.unibo.wastemaster.core.services.InvoiceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract base class for database integration tests. Sets up the EntityManager, DAOs,
 * and service layer managers required for testing. Manages the lifecycle of the
 * persistence context before and after each test.
 */
public abstract class AbstractDatabaseTest {

    /**
     * Shared EntityManagerFactory for all tests.
     */
    private static EntityManagerFactory emf;

    /**
     * EntityManager for the current test.
     */
    private EntityManager em;

    // DAO instances for database access
    private GenericDAO<Location> locationDAO;
    private AccountDAO accountDAO;
    private EmployeeDAO employeeDAO;
    private WasteDAO wasteDAO;
    private CustomerDAO customerDAO;
    private WasteScheduleDAO wasteScheduleDAO;
    private RecurringScheduleDAO recurringScheduleDAO;
    private CollectionDAO collectionDAO;
    private OneTimeScheduleDAO oneTimeScheduleDAO;
    private VehicleDAO vehicleDAO;
    private TripDAO tripDAO;
    private InvoiceDAO invoiceDAO;

    // Service managers for business logic
    private AccountManager accountManager;
    private EmployeeManager employeeManager;
    private WasteManager wasteManager;
    private CustomerManager customerManager;
    private WasteScheduleManager wasteScheduleManager;
    private RecurringScheduleManager recurringScheduleManager;
    private OneTimeScheduleManager oneTimeScheduleManager;
    private CollectionManager collectionManager;
    private VehicleManager vehicleManager;
    private TripManager tripManager;
    private InvoiceManager invoiceManager;

    /**
     * Initializes the EntityManagerFactory before any test runs.
     */
    @BeforeAll
    public static void init() {
        emf = Persistence.createEntityManagerFactory("test-pu");
        if (emf == null) {
            System.out.println("Failed to create EntityManagerFactory.");
        }
    }

    /**
     * Closes the EntityManagerFactory after all tests are completed.
     */
    @AfterAll
    public static void cleanUp() {
        if (emf != null) {
            emf.close();
        }
    }

    /**
     * Returns the shared EntityManagerFactory for all tests.
     *
     * @return the EntityManagerFactory instance
     */
    protected static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Sets up the EntityManager, DAOs, and service managers before each test.
     */
    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();

        wasteDAO = new WasteDAO(em);
        locationDAO = new GenericDAO<>(em, Location.class);
        customerDAO = new CustomerDAO(em);
        accountDAO = new AccountDAO(em);
        employeeDAO = new EmployeeDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        collectionDAO = new CollectionDAO(em);
        vehicleDAO = new VehicleDAO(em);
        tripDAO = new TripDAO(em);

        wasteManager = new WasteManager(wasteDAO);
        customerManager = new CustomerManager(customerDAO);
        accountManager = new AccountManager(accountDAO);
        employeeManager = new EmployeeManager(employeeDAO, em, accountManager);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);

        recurringScheduleManager =
                new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);
        collectionManager =
                new CollectionManager(collectionDAO, recurringScheduleManager);
        recurringScheduleManager.setCollectionManager(collectionManager);
        oneTimeScheduleManager =
                new OneTimeScheduleManager(oneTimeScheduleDAO, collectionManager);
        vehicleManager = new VehicleManager(vehicleDAO);
        tripManager = new TripManager(tripDAO);
    }

    /**
     * Rolls back any active transaction and closes the EntityManager after each test.
     */
    @AfterEach
    public void tearDown() {
        if (em != null) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    /**
     * Returns the EntityManager for the current test.
     *
     * @return the EntityManager instance
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns the DAO for Location entities.
     *
     * @return the Location GenericDAO instance
     */
    protected GenericDAO<Location> getLocationDAO() {
        return locationDAO;
    }

    /**
     * Returns the DAO for Employee entities.
     *
     * @return the EmployeeDAO instance
     */
    protected EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    /**
     * Returns the DAO for Waste entities.
     *
     * @return the WasteDAO instance
     */
    protected WasteDAO getWasteDAO() {
        return wasteDAO;
    }

    /**
     * Returns the DAO for Customer entities.
     *
     * @return the CustomerDAO instance
     */
    protected CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    /**
     * Returns the DAO for WasteSchedule entities.
     *
     * @return the WasteScheduleDAO instance
     */
    protected WasteScheduleDAO getWasteScheduleDAO() {
        return wasteScheduleDAO;
    }

    /**
     * Returns the DAO for RecurringSchedule entities.
     *
     * @return the RecurringScheduleDAO instance
     */
    protected RecurringScheduleDAO getRecurringScheduleDAO() {
        return recurringScheduleDAO;
    }

    /**
     * Returns the DAO for Collection entities.
     *
     * @return the CollectionDAO instance
     */
    protected CollectionDAO getCollectionDAO() {
        return collectionDAO;
    }

    /**
     * Returns the DAO for OneTimeSchedule entities.
     *
     * @return the OneTimeScheduleDAO instance
     */
    protected OneTimeScheduleDAO getOneTimeScheduleDAO() {
        return oneTimeScheduleDAO;
    }

    /**
     * Returns the DAO for Vehicle entities.
     *
     * @return the VehicleDAO instance
     */
    protected VehicleDAO getVehicleDAO() {
        return vehicleDAO;
    }

    /**
     * Returns the DAO for Trip entities.
     *
     * @return the TripDAO instance
     */
    protected TripDAO getTripDAO() {
        return tripDAO;
    }

    /**
     * Returns the manager for Employee-related operations.
     *
     * @return the EmployeeManager instance
     */
    protected EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    /**
     * Returns the manager for Waste-related operations.
     *
     * @return the WasteManager instance
     */
    protected WasteManager getWasteManager() {
        return wasteManager;
    }

    /**
     * Returns the manager for Customer-related operations.
     *
     * @return the CustomerManager instance
     */
    protected CustomerManager getCustomerManager() {
        return customerManager;
    }

    /**
     * Returns the manager for WasteSchedule-related operations.
     *
     * @return the WasteScheduleManager instance
     */
    protected WasteScheduleManager getWasteScheduleManager() {
        return wasteScheduleManager;
    }

    /**
     * Returns the manager for RecurringSchedule-related operations.
     *
     * @return the RecurringScheduleManager instance
     */
    protected RecurringScheduleManager getRecurringScheduleManager() {
        return recurringScheduleManager;
    }

    /**
     * Returns the manager for OneTimeSchedule-related operations.
     *
     * @return the OneTimeScheduleManager instance
     */
    protected OneTimeScheduleManager getOneTimeScheduleManager() {
        return oneTimeScheduleManager;
    }

    /**
     * Returns the manager for Collection-related operations.
     *
     * @return the CollectionManager instance
     */
    protected CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Returns the manager for Vehicle-related operations.
     *
     * @return the VehicleManager instance
     */
    protected VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    /**
     * Returns the manager for Trip-related operations.
     *
     * @return the TripManager instance
     */
    protected TripManager getTripManager() {
        return tripManager;
    }

    /**
     * Returns the DAO for Account entities.
     *
     * @return the AccountDAO instance
     */
    protected AccountDAO getAccountDAO() {
        return accountDAO;
    }

    /**
     * Returns the AccountManager for managing account-related operations.
     *
     * @return the AccountManager instance
     */
    protected AccountManager getAccountManager() {
        return accountManager;
    }
}
