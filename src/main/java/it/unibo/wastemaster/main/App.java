package it.unibo.wastemaster.main;

import it.unibo.wastemaster.database.HibernateUtil;

import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Reservation;
import it.unibo.wastemaster.core.models.Reservation.ReservationStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.Vehicle;


import jakarta.persistence.EntityManager;
import java.util.Date;


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
            Collection wasteCollection  = new Collection(customer, new Date(), Waste.WasteType.PLASTIC, Collection.CollectionStatus.PENDING, 2, 1, false);


            Date bookingDate = new Date();
            Date pickupDate = new Date(System.currentTimeMillis() + 86400000L);

            Reservation reservation = new Reservation(customer, bookingDate, pickupDate, ReservationStatus.PENDING);
            
            Vehicle vehicle = new Vehicle("ABe23CD", 1000, "Fiat", "Panda", 2010, Vehicle.LicenceType.C, Vehicle.VehicleStatus.IN_SERVICE);


            Waste waste = new Waste(Waste.WasteType.PLASTIC, true, false);


            
            entityManager.persist(employee);
            entityManager.persist(customer);
            entityManager.persist(wasteCollection);
            entityManager.persist(reservation);
            entityManager.persist(vehicle);
            entityManager.persist(waste);


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
