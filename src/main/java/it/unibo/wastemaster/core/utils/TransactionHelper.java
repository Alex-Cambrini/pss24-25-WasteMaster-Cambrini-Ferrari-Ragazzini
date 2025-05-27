package it.unibo.wastemaster.core.utils;

import jakarta.persistence.EntityManager;

/**
 * Utility class to execute JPA transactions safely.
 */
public final class TransactionHelper {

    private TransactionHelper() {
        // Prevent instantiation
    }

    /**
     * Executes a transactional operation using the given EntityManager.
     *
     * @param entityManager the EntityManager to use
     * @param operation the operation to execute
     */
    public static void executeTransaction(final EntityManager entityManager,
            final Runnable operation) {
        boolean startedHere = false;

        try {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
                startedHere = true;
            }

            operation.run();

            if (startedHere) {
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
