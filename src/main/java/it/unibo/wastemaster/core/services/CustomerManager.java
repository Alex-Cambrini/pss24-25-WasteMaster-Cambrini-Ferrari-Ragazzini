package it.unibo.wastemaster.core.services;


import it.unibo.wastemaster.core.dao.CustomerDAO;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;


public class CustomerManager {
    private CustomerDAO customerDAO;

    public CustomerManager(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer addCustomer(String name, String surname, String email, String phone, String street, String civicNumber, String city, String postalCode) {
        if(name == null || name.isBlank() || email == null || email.isBlank()){
            throw new IllegalArgumentException("Nome ed email non possono essere vuoti");
        }
        if(customerDAO.existsByEmail(email)){
            throw new IllegalArgumentException("Un cliente con questa email esiste già");
        }
        Location location = new Location(street, civicNumber, city, postalCode);
        Customer customer = new Customer(name, surname, location, email, phone);
        customerDAO.insert(customer);
        return customer;
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
