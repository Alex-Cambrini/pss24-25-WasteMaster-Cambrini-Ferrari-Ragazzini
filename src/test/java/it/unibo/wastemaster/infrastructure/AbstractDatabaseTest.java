package it.unibo.wastemaster.infrastructure;

import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.domain.repository.EmployeeRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.repository.OneTimeScheduleRepository;
import it.unibo.wastemaster.domain.repository.RecurringScheduleRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.domain.repository.VehicleRepository;
import it.unibo.wastemaster.domain.repository.WasteRepository;
import it.unibo.wastemaster.domain.repository.WasteScheduleRepository;
import it.unibo.wastemaster.domain.repository.impl.AccountRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.CollectionRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.CustomerRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.EmployeeRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.InvoiceRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.OneTimeScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.RecurringScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.TripRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.VehicleRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.WasteRepositoryImpl;
import it.unibo.wastemaster.domain.repository.impl.WasteScheduleRepositoryImpl;
import it.unibo.wastemaster.domain.service.AccountManager;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.EmployeeManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.OneTimeScheduleManager;
import it.unibo.wastemaster.domain.service.RecurringScheduleManager;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.domain.service.WasteManager;
import it.unibo.wastemaster.domain.service.WasteScheduleManager;
import it.unibo.wastemaster.infrastructure.dao.AccountDAO;
import it.unibo.wastemaster.infrastructure.dao.CollectionDAO;
import it.unibo.wastemaster.infrastructure.dao.CustomerDAO;
import it.unibo.wastemaster.infrastructure.dao.EmployeeDAO;
import it.unibo.wastemaster.infrastructure.dao.GenericDAO;
import it.unibo.wastemaster.infrastructure.dao.InvoiceDAO;
import it.unibo.wastemaster.infrastructure.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.infrastructure.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.infrastructure.dao.TripDAO;
import it.unibo.wastemaster.infrastructure.dao.VehicleDAO;
import it.unibo.wastemaster.infrastructure.dao.WasteDAO;
import it.unibo.wastemaster.infrastructure.dao.WasteScheduleDAO;
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

    // Repository instances for data access abstraction
    private AccountRepository accountRepository;
    private EmployeeRepository employeeRepository;
    private WasteRepository wasteRepository;
    private CustomerRepository customerRepository;
    private WasteScheduleRepository wasteScheduleRepository;
    private RecurringScheduleRepository recurringScheduleRepository;
    private CollectionRepository collectionRepository;
    private OneTimeScheduleRepository oneTimeScheduleRepository;
    private VehicleRepository vehicleRepository;
    private TripRepository tripRepository;
    private InvoiceRepository invoiceRepository;

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
            throw new IllegalStateException("Failed to create EntityManagerFactory.");
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
     * Initializes the EntityManager and begins a transaction for the current test.
     * Also sets up all DAOs, repositories, and service managers required for testing.
     */
    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();

        // DAO init
        locationDAO = new GenericDAO<>(em, Location.class);
        accountDAO = new AccountDAO(em);
        employeeDAO = new EmployeeDAO(em);
        wasteDAO = new WasteDAO(em);
        customerDAO = new CustomerDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        collectionDAO = new CollectionDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        vehicleDAO = new VehicleDAO(em);
        tripDAO = new TripDAO(em);
        invoiceDAO = new InvoiceDAO(em);

        // Repository init con implementazioni concrete
        accountRepository = new AccountRepositoryImpl(accountDAO);
        employeeRepository = new EmployeeRepositoryImpl(employeeDAO);
        wasteRepository = new WasteRepositoryImpl(wasteDAO);
        customerRepository = new CustomerRepositoryImpl(customerDAO);
        wasteScheduleRepository = new WasteScheduleRepositoryImpl(wasteScheduleDAO);
        recurringScheduleRepository =
                new RecurringScheduleRepositoryImpl(recurringScheduleDAO);
        collectionRepository = new CollectionRepositoryImpl(collectionDAO);
        oneTimeScheduleRepository = new OneTimeScheduleRepositoryImpl(oneTimeScheduleDAO);
        vehicleRepository = new VehicleRepositoryImpl(vehicleDAO);
        tripRepository = new TripRepositoryImpl(tripDAO);
        invoiceRepository = new InvoiceRepositoryImpl(invoiceDAO);

        // Managers init (pass repositories)
        accountManager = new AccountManager(accountRepository);
        employeeManager = new EmployeeManager(employeeRepository, accountManager);
        wasteManager = new WasteManager(wasteRepository);
        customerManager = new CustomerManager(customerRepository);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleRepository);
        recurringScheduleManager =
                new RecurringScheduleManager(recurringScheduleRepository,
                        wasteScheduleManager);
        collectionManager =
                new CollectionManager(collectionRepository, recurringScheduleManager);
        recurringScheduleManager.setCollectionManager(collectionManager);
        oneTimeScheduleManager =
                new OneTimeScheduleManager(oneTimeScheduleRepository, collectionManager);
        vehicleManager = new VehicleManager(vehicleRepository);
        tripManager = new TripManager(tripRepository, collectionRepository,
                recurringScheduleManager);
        invoiceManager = new InvoiceManager(invoiceRepository);
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
     * Returns the DAO for Invoice entities.
     *
     * @return the InvoiceDAO instance
     */
    protected InvoiceDAO getInvoiceDAO() {
        return invoiceDAO;
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
     * Returns the DaoManager instance.
     *
     * @return the DaoManager instance
     */
    protected InvoiceManager getInvoiceManager() {
        return invoiceManager;
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

    /**
     * Returns the repository for Customer entities.
     *
     * @return the CustomerRepository instance
     */
    protected CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    /**
     * Returns the repository for Invoice entities.
     *
     * @return the InvoiceRepository instance
     */
    protected InvoiceRepository getInvoiceRepository() {
        return invoiceRepository;
    }

    /**
     * Returns the repository for Trip entities.
     *
     * @return the TripRepository instance
     */
    protected TripRepository getTripRepository() {
        return tripRepository;
    }
}
