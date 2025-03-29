package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Customer;
import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.database.HibernateUtil;

public class CustomerManager {
    private final GenericDAO<Customer> customerDAO;
    private final EntityManager entityManager;
    
    public CustomerManager() {
        entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        customerDAO = new GenericDAO<>(entityManager, Customer.class);
    }
}
