package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.CustomerDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import java.util.List;

/**
 * Manages operations related to Customer entities.
 */
public class CustomerManager {

    private CustomerDAO customerDAO;

    /**
     * Constructs a CustomerManager with the specified DAO.
     *
     * @param customerDAO the DAO used for Customer persistence
     */
    public CustomerManager(final CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * Adds a new customer if the email is not already registered.
     *
     * @param customer the customer to add
     * @return the added customer
     * @throws IllegalArgumentException if the email is already in use
     */
    public Customer addCustomer(final Customer customer) {
        if (isEmailRegistered(customer.getEmail())) {
            throw new IllegalArgumentException(String.format(
                    "Cannot add customer: the email address '%s' is already in use.",
                    customer.getEmail()));
        }
        customerDAO.insert(customer);
        return customer;
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    private boolean isEmailRegistered(final String email) {
        return customerDAO.existsByEmail(email);
    }

    /**
     * Retrieves a customer by its ID.
     *
     * @param customerId the ID of the customer
     * @return the customer with the given ID, or null if not found
     */
    public Customer getCustomerById(final int customerId) {
        return customerDAO.findById(customerId);
    }

    /**
     * Updates the given customer.
     *
     * @param toUpdateCustomer the customer with updated data
     * @throws IllegalArgumentException if the customer is invalid or the email is used by
     *         another customer
     */
    public void updateCustomer(final Customer toUpdateCustomer) {
        ValidateUtils.validateEntity(toUpdateCustomer);
        ValidateUtils.requireArgNotNull(toUpdateCustomer.getCustomerId(),
                "Customer ID cannot be null");
        Customer existing = customerDAO.findByEmail(toUpdateCustomer.getEmail());
        if (existing != null
                && !existing.getCustomerId().equals(toUpdateCustomer.getCustomerId())) {
            throw new IllegalArgumentException(
                    "Email is already used by another customer.");
        }
        customerDAO.update(toUpdateCustomer);
    }

    /**
     * Performs a soft delete on the customer by marking it deleted and updating it.
     *
     * @param customer the customer to soft delete
     * @return true if deletion succeeded, false if any validation fails
     */
    public boolean softDeleteCustomer(final Customer customer) {
        try {
            ValidateUtils.requireArgNotNull(customer, "Customer cannot be null");
            ValidateUtils.requireArgNotNull(customer.getCustomerId(),
                    "Customer ID cannot be null");
            customer.delete();
            updateCustomer(customer);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns all customers.
     *
     * @return list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
