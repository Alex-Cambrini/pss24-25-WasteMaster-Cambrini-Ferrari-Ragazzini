package it.unibo.wastemaster.main;

import it.unibo.wastemaster.database.HibernateUtil;
import jakarta.persistence.EntityManager;

public class App {
    public static void main(String[] args) {
        System.out.println("Test di connessione al database con Hibernate!");

        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.createNativeQuery("SELECT 1").getResultList();
            entityManager.getTransaction().commit();
            System.out.println("Connessione al database riuscita!");
        } catch (Exception e) {
            System.err.println("Errore di connessione: " + e.getMessage());
        } finally {
            entityManager.close();
            HibernateUtil.shutdown();
        }
    }
}