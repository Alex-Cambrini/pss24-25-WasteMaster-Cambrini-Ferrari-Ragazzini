package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.database.HibernateUtil;

public class CustomerManager {
    private final GenericDAO<Customer> customerDAO;
    private final EntityManager entityManager;
    
    public CustomerManager() {
        entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        customerDAO = new GenericDAO<>(entityManager, Customer.class);
    }

    public void addCustomer(String name, String surname, String email, String phone, String street, String civicNumber, String city, String postalCode) {
        Location location = new Location(street, civicNumber, city, postalCode);
		Customer customer = new Customer(name, surname, location, email, phone);
        customerDAO.insert(customer);
    }
}
