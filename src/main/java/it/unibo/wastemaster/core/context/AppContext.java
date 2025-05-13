package it.unibo.wastemaster.core.context;

import it.unibo.wastemaster.core.dao.*;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.services.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class AppContext {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    // DAOs
    public static EmployeeDAO employeeDAO;
    public static GenericDAO<Waste> wasteDAO;
    public static CustomerDAO customerDAO;
    public static WasteScheduleDAO wasteScheduleDAO;
    public static RecurringScheduleDAO recurringScheduleDAO;
    public static CollectionDAO collectionDAO;
    public static OneTimeScheduleDAO oneTimeScheduleDAO;
    public static VehicleDAO vehicleDAO;

    // Services
    public static CustomerManager customerManager;
    public static WasteScheduleManager wasteScheduleManager;
    public static RecurringScheduleManager recurringScheduleManager;
    public static CollectionManager collectionManager;
    public static VehicleManager vehicleManager;
    public static EmployeeManager employeeManager;



    public static void init() {
        emf = Persistence.createEntityManagerFactory("myJpaUnit");
        em = emf.createEntityManager();
        
        // Inizializzazione dei DAO
        employeeDAO = new EmployeeDAO(em);
        wasteDAO = new GenericDAO<>(em, Waste.class);
        customerDAO = new CustomerDAO(em);
        wasteScheduleDAO = new WasteScheduleDAO(em);
        recurringScheduleDAO = new RecurringScheduleDAO(em);
        collectionDAO = new CollectionDAO(em);
        oneTimeScheduleDAO = new OneTimeScheduleDAO(em);
        vehicleDAO = new VehicleDAO(em);
        
        // Inizializzazione dei Services
        customerManager = new CustomerManager(customerDAO);
        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
        recurringScheduleManager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager);
        collectionManager = new CollectionManager(collectionDAO, recurringScheduleManager);
        vehicleManager = new VehicleManager(vehicleDAO);
        employeeManager = new EmployeeManager(employeeDAO);

        
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
