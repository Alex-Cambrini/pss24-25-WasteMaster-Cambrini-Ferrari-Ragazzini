package it.unibo.wastemaster.core.context;

import it.unibo.wastemaster.core.dao.*;
import it.unibo.wastemaster.core.services.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AppContext {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    // DAOs
    public static EmployeeDAO employeeDAO;
    public static WasteDAO wasteDAO;
    public static CustomerDAO customerDAO;
    public static WasteScheduleDAO wasteScheduleDAO;
    public static RecurringScheduleDAO recurringScheduleDAO;
    public static ScheduleDAO scheduleDAO;
    public static CollectionDAO collectionDAO;
    public static OneTimeScheduleDAO oneTimeScheduleDAO;
    public static VehicleDAO vehicleDAO;

    // Services
    public static EmployeeManager employeeManager;
    public static WasteManager wasteManager;
    public static CustomerManager customerManager;
    public static WasteScheduleManager wasteScheduleManager;
    public static RecurringScheduleManager recurringScheduleManager;
    public static OneTimeScheduleManager oneTimeScheduleManager;
    public static CollectionManager collectionManager;
    public static VehicleManager vehicleManager;




    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();
        
        // Inizializzazione dei DAO
        employeeDAO = new EmployeeDAO(em);
        wasteDAO = new WasteDAO(em);
        customerDAO = new CustomerDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        scheduleDAO = new ScheduleDAO(em);
        collectionDAO = new CollectionDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        vehicleDAO = new VehicleDAO(em);
        
        // Inizializzazione dei Services
        employeeManager = new EmployeeManager(employeeDAO);
        wasteManager = new WasteManager(wasteDAO);
        customerManager = new CustomerManager(customerDAO);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
        recurringScheduleManager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);
        collectionManager = new CollectionManager(collectionDAO, recurringScheduleManager);
        oneTimeScheduleManager = new OneTimeScheduleManager(oneTimeScheduleDAO, collectionManager);
        vehicleManager = new VehicleManager(vehicleDAO);


        
        // Collegamento
        recurringScheduleManager.setCollectionManager(collectionManager);
    }

    public static EntityManager getEntityManager() {
        return em;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
