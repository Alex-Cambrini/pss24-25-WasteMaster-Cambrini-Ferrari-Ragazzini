package it.unibo.wastemaster.core.utils;

import jakarta.persistence.EntityManager;

public class TransactionHelper {
    public static void executeTransaction(EntityManager entityManager, Runnable operation) {
        try {
            entityManager.getTransaction().begin();
            operation.run();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
