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
        if (updateCustomer == null || updateCustomer.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer or customer ID cannot be null");
        }
    
        Customer existing = customerDAO.findById(updateCustomer.getCustomerId());
        if (existing == null) {
            throw new IllegalArgumentException("Customer not found");
        }
    
        if (!existing.getEmail().equals(updateCustomer.getEmail())
                && customerDAO.existsByEmail(updateCustomer.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
    
        it.unibo.wastemaster.core.utils.ValidateUtils.VALIDATOR.validate(updateCustomer).stream().findFirst().ifPresent(v -> {
            throw new jakarta.validation.ConstraintViolationException("Validation failed", java.util.Set.of(v));
        });
    
        customerDAO.update(updateCustomer);
    }
    

    public boolean deleteCustomer(Customer customer) {
        if (customer != null && customer.getCustomerId() != null) {
            Customer managed = customerDAO.findById(customer.getCustomerId());
            if (managed != null) {
                customerDAO.delete(managed);
                return true;
            }
        }
        return false;
    }  

}
