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
 * Application context class that manages global state and resources
 * such as the {@link EntityManager}, {@link ServiceFactory}, and
 * the current {@link Account}.
 * This class is {@code final} and provides only static utility methods.
 * It cannot be instantiated.
 */
public final class AppContext {

    /**
     * The currently logged-in account, or {@code null} if no user is authenticated.
     */
    private static Account currentAccount;

    /**
     * The {@link EntityManagerFactory} used for creating {@link EntityManager}
     * instances.
     */
    private static EntityManagerFactory emf;

    /**
     * The shared {@link EntityManager} instance for the application.
     */
    private static EntityManager em;

    /**
     * The primary JavaFX stage (window) used as owner for dialogs.
     */
    private static Stage owner;

    /**
     * The service factory that provides access to domain managers and services.
     */
    private static ServiceFactory serviceFactory;

    /**
     * Private constructor to prevent instantiation.
     */
    private AppContext() {
        // Prevent instantiation
    }

    /**
     * Initializes the application context by creating the
     * {@link EntityManagerFactory},
     * the shared {@link EntityManager}, and the {@link ServiceFactory}.
     * Also ensures that a default administrator account exists.
     */
    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();

        serviceFactory = new ServiceFactory(em);
        createDefaultAccount();
    }

    /**
     * Gets the {@link ServiceFactory} instance for accessing managers and services.
     *
     * @return the service factory
     */
    public static ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * Gets the shared {@link EntityManager} instance.
     *
     * @return the entity manager
     */
    public static EntityManager getEntityManager() {
        return em;
    }

    /**
     * Gets the {@link EntityManagerFactory}.
     *
     * @return the entity manager factory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Gets the current JavaFX {@link Stage} set as owner for dialogs.
     *
     * @return the owner stage
     */
    public static Stage getOwner() {
        return owner;
    }

    /**
     * Sets the current JavaFX {@link Stage} as the owner for dialogs.
     *
     * @param stage the stage to set as owner
     */
    public static void setOwner(final Stage stage) {
        owner = stage;
    }

    /**
     * Gets the currently logged-in {@link Account}.
     *
     * @return the current account, or {@code null} if none is set
     */
    public static Account getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Ensures that a default administrator account exists.
     * If no admin account is found, one is created with default credentials.
     */
    private static void createDefaultAccount() {
        boolean exists = serviceFactory.getAccountManager()
                .findAccountByEmployeeEmail("admin@admin.com")
                .isPresent();

        if (!exists) {
            Employee admin = new Employee(
                    "Admin", "Admin",
                    new Location("Default", "0", "Default", "12345"),
                    "admin@admin.com",
                    "0000000000",
                    Employee.Role.ADMINISTRATOR,
                    Employee.Licence.NONE);
            serviceFactory.getEmployeeManager().addEmployee(admin, "admin123");
        }
    }

    /**
     * Sets the currently logged-in {@link Account}.
     *
     * @param account the account to set as current
     */
    public static void setCurrentAccount(final Account account) {
        currentAccount = account;
    }
}
