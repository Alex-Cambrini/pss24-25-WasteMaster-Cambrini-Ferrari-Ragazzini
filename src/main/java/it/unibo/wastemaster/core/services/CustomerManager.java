package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class CustomerManager {
    private CustomerDAO customerDAO;

    public CustomerManager(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer addCustomer(Customer customer) {      
        if (isEmailRegistered(customer.getEmail())) {
            throw new IllegalArgumentException(
                String.format("Cannot add customer: the email address '%s' is already in use.", 
                              customer.getEmail())
            );
        }
        customerDAO.insert(customer);
        return customer;
    }    

    private boolean isEmailRegistered(String email) {
        return customerDAO.existsByEmail(email);
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.findById(customerId);
    }

    public void updateCustomer(Customer toUpdateCustomer) {
        ValidateUtils.validateEntity(toUpdateCustomer);
        ValidateUtils.requireArgNotNull(toUpdateCustomer, "Customer o ID non possono essere null");
        ValidateUtils.requireArgNotNull(toUpdateCustomer.getCustomerId(), "Customer ID non può essere null");
        customerDAO.update(toUpdateCustomer);
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
