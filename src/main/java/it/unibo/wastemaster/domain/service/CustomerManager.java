package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import java.util.Optional;

/**
 * Manages operations related to Customer entities.
 */
public class CustomerManager {

    private final CustomerRepository customerRepository;

    public CustomerManager(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
        customerRepository.save(customer);
        return customer;
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    private boolean isEmailRegistered(final String email) {
        return customerRepository.existsByEmail(email);
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
        Optional<Customer> existingOpt =
                customerRepository.findByEmail(toUpdateCustomer.getEmail());
        if (existingOpt.isPresent() && !existingOpt.get().getCustomerId()
                .equals(toUpdateCustomer.getCustomerId())) {
            throw new IllegalArgumentException(
                    "Email is already used by another customer.");
        }
        customerRepository.update(toUpdateCustomer);
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
}
