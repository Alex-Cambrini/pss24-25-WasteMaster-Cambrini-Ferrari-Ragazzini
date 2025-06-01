package it.unibo.wastemaster.core.context;

import it.unibo.wastemaster.core.dao.AccountDAO;
import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.dao.EmployeeDAO;
import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.ScheduleDAO;
import it.unibo.wastemaster.core.dao.VehicleDAO;
import it.unibo.wastemaster.core.dao.WasteDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.services.AccountManager;
import it.unibo.wastemaster.core.services.CollectionManager;
import it.unibo.wastemaster.core.services.CustomerManager;
import it.unibo.wastemaster.core.services.EmployeeManager;
import it.unibo.wastemaster.core.services.OneTimeScheduleManager;
import it.unibo.wastemaster.core.services.RecurringScheduleManager;
import it.unibo.wastemaster.core.services.VehicleManager;
import it.unibo.wastemaster.core.services.WasteManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.stage.Stage;

/**
 * Global application context responsible for initializing and managing DAOs, services,
 * and shared resources like EntityManager.
 */
public final class AppContext {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Stage owner;

    // DAOs
    private static AccountDAO accountDAO;
    private static EmployeeDAO employeeDAO;
    private static WasteDAO wasteDAO;
    private static CustomerDAO customerDAO;
    private static WasteScheduleDAO wasteScheduleDAO;
    private static RecurringScheduleDAO recurringScheduleDAO;
    private static ScheduleDAO scheduleDAO;
    private static CollectionDAO collectionDAO;
    private static OneTimeScheduleDAO oneTimeScheduleDAO;
    private static VehicleDAO vehicleDAO;

    // Services
    private static AccountManager accountManager;
    private static EmployeeManager employeeManager;
    private static WasteManager wasteManager;
    private static CustomerManager customerManager;
    private static WasteScheduleManager wasteScheduleManager;
    private static RecurringScheduleManager recurringScheduleManager;
    private static OneTimeScheduleManager oneTimeScheduleManager;
    private static CollectionManager collectionManager;
    private static VehicleManager vehicleManager;

    private AppContext() {
        // Prevent instantiation
    }

    /**
     * Sets the main application window (owner).
     *
     * @param stage the primary stage of the application
     */
    public static void setOwner(final Stage stage) {
        owner = stage;
    }

    /**
     * Returns the main application window (owner).
     *
     * @return the primary stage of the application
     */
    public static Stage getOwner() {
        return owner;
    }

    /**
     * Initializes the persistence context, DAOs, and services. Must be called once at
     * application startup.
     */
    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();

        // Initialize DAOs
        accountDAO = new AccountDAO(em);
        employeeDAO = new EmployeeDAO(em);
        wasteDAO = new WasteDAO(em);
        customerDAO = new CustomerDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        scheduleDAO = new ScheduleDAO(em);
        collectionDAO = new CollectionDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        vehicleDAO = new VehicleDAO(em);

        // Initialize Services
        accountManager = new AccountManager(accountDAO);
        employeeManager = new EmployeeManager(employeeDAO, em, accountManager);
        wasteManager = new WasteManager(wasteDAO);
        customerManager = new CustomerManager(customerDAO);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
        recurringScheduleManager =
                new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);
        collectionManager =
                new CollectionManager(collectionDAO, recurringScheduleManager);
        oneTimeScheduleManager =
                new OneTimeScheduleManager(oneTimeScheduleDAO, collectionManager);
        vehicleManager = new VehicleManager(vehicleDAO);

        // Link dependencies
        recurringScheduleManager.setCollectionManager(collectionManager);
        createDefaultAccount();
    }

    private static void createDefaultAccount() {
        boolean exists = accountDAO.findAccountByEmployeeEmail("admin@admin.com").isPresent();
        if (!exists) {
            Employee admin = new Employee(
                    "Admin", "Admin",
                    new Location("Default", "0", "Default", "12345"),
                    "admin@admin.com",
                    "0000000000",
                    Employee.Role.ADMINISTRATOR,
                    Employee.Licence.NONE
            );
            employeeManager.addEmployee(admin, "admin123");
        }
    }

    /**
     * Returns the shared EntityManager instance.
     *
     * @return the EntityManager used by DAOs and services
     */
    public static EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns the EntityManagerFactory used to create EntityManager instances.
     *
     * @return the EntityManagerFactory instance
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    // DAOs getters

    /**
     * Returns the EmployeeDAO instance.
     *
     * @return the EmployeeDAO
     */
    public static EmployeeDAO getEmployeeDAO() {
        return employeeDAO;
    }

    /**
     * Returns the WasteDAO instance.
     *
     * @return the WasteDAO
     */
    public static WasteDAO getWasteDAO() {
        return wasteDAO;
    }

    /**
     * Returns the CustomerDAO instance.
     *
     * @return the CustomerDAO
     */
    public static CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    /**
     * Returns the WasteScheduleDAO instance.
     *
     * @return the WasteScheduleDAO
     */
    public static WasteScheduleDAO getWasteScheduleDAO() {
        return wasteScheduleDAO;
    }

    /**
     * Returns the RecurringScheduleDAO instance.
     *
     * @return the RecurringScheduleDAO
     */
    public static RecurringScheduleDAO getRecurringScheduleDAO() {
        return recurringScheduleDAO;
    }

    /**
     * Returns the ScheduleDAO instance.
     *
     * @return the ScheduleDAO
     */
    public static ScheduleDAO getScheduleDAO() {
        return scheduleDAO;
    }

    /**
     * Returns the CollectionDAO instance.
     *
     * @return the CollectionDAO
     */
    public static CollectionDAO getCollectionDAO() {
        return collectionDAO;
    }

    /**
     * Returns the OneTimeScheduleDAO instance.
     *
     * @return the OneTimeScheduleDAO
     */
    public static OneTimeScheduleDAO getOneTimeScheduleDAO() {
        return oneTimeScheduleDAO;
    }

    /**
     * Returns the VehicleDAO instance.
     *
     * @return the VehicleDAO
     */
    public static VehicleDAO getVehicleDAO() {
        return vehicleDAO;
    }

    // Services getters

    /**
     * Returns the EmployeeManager instance.
     *
     * @return the EmployeeManager
     */
    public static EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    /**
     * Returns the WasteManager instance.
     *
     * @return the WasteManager
     */
    public static WasteManager getWasteManager() {
        return wasteManager;
    }

    /**
     * Returns the CustomerManager instance.
     *
     * @return the CustomerManager
     */
    public static CustomerManager getCustomerManager() {
        return customerManager;
    }

    /**
     * Returns the WasteScheduleManager instance.
     *
     * @return the WasteScheduleManager
     */
    public static WasteScheduleManager getWasteScheduleManager() {
        return wasteScheduleManager;
    }

    /**
     * Returns the RecurringScheduleManager instance.
     *
     * @return the RecurringScheduleManager
     */
    public static RecurringScheduleManager getRecurringScheduleManager() {
        return recurringScheduleManager;
    }

    /**
     * Returns the OneTimeScheduleManager instance.
     *
     * @return the OneTimeScheduleManager
     */
    public static OneTimeScheduleManager getOneTimeScheduleManager() {
        return oneTimeScheduleManager;
    }

    /**
     * Returns the CollectionManager instance.
     *
     * @return the CollectionManager
     */
    public static CollectionManager getCollectionManager() {
        return collectionManager;
    }

    /**
     * Returns the VehicleManager instance.
     *
     * @return the VehicleManager
     */
    public static VehicleManager getVehicleManager() {
        return vehicleManager;
    }
}
