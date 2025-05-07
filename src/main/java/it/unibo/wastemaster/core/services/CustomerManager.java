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
                            customer.getEmail()));
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
        ValidateUtils.requireArgNotNull(toUpdateCustomer.getCustomerId(), "Customer ID cannot be null");
        Customer existing = customerDAO.findByEmail(toUpdateCustomer.getEmail());
        if (existing != null && !existing.getCustomerId().equals(toUpdateCustomer.getCustomerId())) {
            throw new IllegalArgumentException("Email is already used by another customer.");
        }
        customerDAO.update(toUpdateCustomer);
    }

    public boolean softDeleteCustomer(Customer customer) {
        try {
            ValidateUtils.requireArgNotNull(customer, "Customer cannot be null");
            ValidateUtils.requireArgNotNull(customer.getCustomerId(), "Customer ID cannot be null");
            customer.delete();
            updateCustomer(customer);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
