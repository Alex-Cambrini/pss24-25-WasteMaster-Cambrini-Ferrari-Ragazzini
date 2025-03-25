package it.unibo.wastemaster.main;

import it.unibo.wastemaster.database.HibernateUtil;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import jakarta.persistence.EntityManager;

public class App {
    public static void main(String[] args) {
        System.out.println("Test di connessione al database con Hibernate!");

        // Crea una connessione al database
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            // Avvia una transazione
            entityManager.getTransaction().begin();

            Location location = new Location( "Via Roma", "10", "Milano", "Italy");
            Customer customer = new Customer("Mario", "Rossi", location, "mario@example.com", "1234567890");
            Employee employee = new Employee("Giuseppe", "Verdi", location, "giuseppe@example.com", "0987654321", Employee.Role.OPERATOR);
            
            entityManager.persist(employee);
            entityManager.persist(customer);

            entityManager.getTransaction().commit();
            
            System.out.println("Connessione al database riuscita e tabella creata!");
        } catch (Exception e) {
            System.err.println("Errore di connessione: " + e.getMessage());
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
         finally {
            entityManager.close();
            HibernateUtil.shutdown();
        }
    }
}
