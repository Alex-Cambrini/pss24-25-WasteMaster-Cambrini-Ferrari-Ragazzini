package it.unibo.wastemaster.application.context;

import it.unibo.wastemaster.infrastructure.di.ServiceFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.stage.Stage;

public final class AppContext {

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

    public static void setOwner(Stage stage) {
        owner = stage;
    }

    public static Stage getOwner() {
        return owner;
    }
}
