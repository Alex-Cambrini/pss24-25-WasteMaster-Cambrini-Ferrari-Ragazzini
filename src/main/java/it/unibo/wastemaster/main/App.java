package it.unibo.wastemaster.main;

import it.unibo.wastemaster.database.HibernateUtil;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.*;
import it.unibo.wastemaster.core.services.ScheduleManager;
import it.unibo.wastemaster.core.services.WasteScheduleManager;
import jakarta.persistence.EntityManager;
import java.util.Date;

public class App {
    public static void main(String[] args) {
        System.out.println("Test di connessione al database con Hibernate!");

        // Crea una connessione al database
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        WasteScheduleDAO wasteScheduleDAO = new WasteScheduleDAO(entityManager);
        WasteScheduleManager wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);

        // Nuovo ScheduleManager che gestisce entrambi i tipi di schedule
        GenericDAO<RecurringSchedule> recurringScheduleDAO = new GenericDAO<>(entityManager, RecurringSchedule.class);
        GenericDAO<OneTimeSchedule> oneTimeScheduleDAO = new GenericDAO<>(entityManager, OneTimeSchedule.class);
        ScheduleManager scheduleManager = new ScheduleManager(wasteScheduleManager, recurringScheduleDAO, oneTimeScheduleDAO);

        try {
            // Avvia una transazione
            entityManager.getTransaction().begin();

            // Creazione della Location e Customer
            Location location = new Location("Via Roma", "10", "Milano", "Italy");
            Customer customer = new Customer("Mario", "Rossi", location, "mario@example.com", "1234567890");

            // Persisti la location e il customer
            entityManager.persist(location);
            entityManager.persist(customer);

            // Aggiungi un nuovo tipo di Waste (PLASTIC)
            Waste plasticWaste = new Waste(Waste.WasteType.PLASTIC, true, false);
            entityManager.persist(plasticWaste); // Salva il tipo di Waste

            // Crea una nuova programmazione (WasteSchedule) per il rifiuto PLASTIC il Venerdì (6)
            WasteSchedule wasteSchedule = new WasteSchedule(plasticWaste, 6); // 6 corrisponde al Venerdì
            entityManager.persist(wasteSchedule); // Salva la WasteSchedule

            // Creazione di una nuova programmazione ricorrente (monthly)


            scheduleManager.createRecurringSchedule(customer, Waste.WasteType.PLASTIC, Schedule.ScheduleStatus.ACTIVE, RecurringSchedule.Frequency.MONTHLY);

            // Creazione di una nuova programmazione una tantum (reservation)
            Date reservationDate = new Date(); // Data della prenotazione (esempio)

            scheduleManager.createOneTimeSchedule(customer, Waste.WasteType.PLASTIC, Schedule.ScheduleStatus.ACTIVE, reservationDate);


            // Commit della transazione
            entityManager.getTransaction().commit();

            System.out.println("Waste e WasteSchedule aggiunti con successo!");

        } catch (Exception e) {
            System.err.println("Errore di connessione: " + e.getMessage());
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
            HibernateUtil.shutdown();
        }
    }
}
