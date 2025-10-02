package it.unibo.wastemaster.application.context;

import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.infrastructure.di.ServiceFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.stage.Stage;

/**
 * Application context singleton for managing global application state,
 * including the current account, persistence, and service factory.
 */
public final class AppContext {

    private static Account currentAccount;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Stage owner;
    private static ServiceFactory serviceFactory;

    private AppContext() {
        // Prevent instantiation
    }

    /**
     * Initializes the application context, persistence, and default admin account.
     */
    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();

        serviceFactory = new ServiceFactory(em);
        createDefaultAccount();
    }

    /**
     * Returns the application's service factory.
     *
     * @return the ServiceFactory instance
     */
    public static ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * Returns the application's EntityManager.
     *
     * @return the EntityManager instance
     */
    public static EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns the application's EntityManagerFactory.
     *
     * @return the EntityManagerFactory instance
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Returns the main application window (owner stage).
     *
     * @return the owner Stage
     */
    public static Stage getOwner() {
        return owner;
    }

    /**
     * Sets the main application window (owner stage).
     *
     * @param stage the Stage to set as owner
     */
    public static void setOwner(Stage stage) {
        owner = stage;
    }

    /**
     * Returns the currently logged-in account.
     *
     * @return the current Account
     */
    public static Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Sets the currently logged-in account.
     *
     * @param account the Account to set as current
     */
    public static void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    /**
     * Creates a default admin account if it does not already exist.
     */
    private static void createDefaultAccount() {
        boolean exists =
                serviceFactory.getAccountManager().findAccountByEmployeeEmail("admin@admin.com").isPresent();
        if (!exists) {
            Employee admin = new Employee(
                    "Admin", "Admin",
                    new Location("Default", "0", "Default", "12345"),
                    "admin@admin.com",
                    "0000000000",
                    Employee.Role.ADMINISTRATOR,
                    Employee.Licence.NONE
            );
            serviceFactory.getEmployeeManager().addEmployee(admin, "admin123");
        }
    }
}
