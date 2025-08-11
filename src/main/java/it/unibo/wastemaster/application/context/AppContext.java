package it.unibo.wastemaster.application.context;

import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.infrastructure.di.ServiceFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.stage.Stage;

public final class AppContext {

    private static Account currentAccount;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static Stage owner;
    private static ServiceFactory serviceFactory;

    private AppContext() {
        // Prevent instantiation
    }

    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();

        serviceFactory = new ServiceFactory(em);
        createDefaultAccount();
    }

    public static ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public static EntityManager getEntityManager() {
        return em;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static Stage getOwner() {
        return owner;
    }

    public static void setOwner(Stage stage) {
        owner = stage;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    private static void createDefaultAccount() {
        boolean exists =
                serviceFactory.getAccountManager().findAccountByEmployeeEmail ("admin@admin.com").isPresent();
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

    public static void setCurrentAccount(Account account) {
        currentAccount = account;
    }
}
