package it.unibo.wastemaster.core.utils;

import jakarta.persistence.EntityManager;

public class TransactionHelper {

	public static void executeTransaction(EntityManager entityManager, Runnable operation) {
        
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