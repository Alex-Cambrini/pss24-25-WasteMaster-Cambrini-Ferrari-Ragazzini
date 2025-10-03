package it.unibo.wastemaster.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class to manage the Hibernate EntityManagerFactory.
 * Provides methods to obtain and close the singleton EntityManagerFactory instance
 * used throughout the application for JPA operations.
 */
public final class HibernateUtil {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
            .createEntityManagerFactory("myJpaUnit");

    private HibernateUtil() {
        // Prevent instantiation
    }

    /**
     * Returns the singleton EntityManagerFactory instance.
     *
     * @return the EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    /**
     * Closes the EntityManagerFactory if it is open.
     */
    public static void shutdown() {
        if (ENTITY_MANAGER_FACTORY != null && ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
