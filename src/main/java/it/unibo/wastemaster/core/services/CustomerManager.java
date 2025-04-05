package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import it.unibo.wastemaster.database.HibernateUtil;

public class CustomerManager {
    private final GenericDAO<Customer> customerDAO;
    private final EntityManager entityManager;
    
    public CustomerManager() {
	this(HibernateUtil.getEntityManagerFactory());
    }

    public CustomerManager(EntityManagerFactory emf) {
        this.entityManager = emf.createEntityManager();
        this.customerDAO = new GenericDAO<>(entityManager, Customer.class);
    }

    public Customer addCustomer(String name, String surname, String email, String phone, String street, String civicNumber, String city, String postalCode) {
        if(name == null || name.isBlank() || email == null || email.isBlank()){
            throw new IllegalArgumentException("Nome ed email non possono essere vuoti");
        }
        if(existsByEmail(email)){
            throw new IllegalArgumentException("Un cliente con questa email esiste già");
        }
        Location location = new Location(street, civicNumber, city, postalCode);
        Customer customer = new Customer(name, surname, location, email, phone);
        customerDAO.insert(customer);
        return customer;
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager
        .createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class)
        .setParameter("email", email)
        .getSingleResult();
        return count > 0;
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.findById(customerId);
    }

    public void updateCustomer(Customer updateCustomer) {
        customerDAO.update(updateCustomer);
    }

    public void deleteCustomer(Customer customer) {
        customerDAO.delete(customer);
    }
}
