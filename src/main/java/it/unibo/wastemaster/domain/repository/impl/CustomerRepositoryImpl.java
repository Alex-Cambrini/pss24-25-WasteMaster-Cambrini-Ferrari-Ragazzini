package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.infrastructure.dao.CustomerDAO;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CustomerRepository} that uses {@link CustomerDAO}
 * to perform CRUD operations on Customer entities.
 */
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerDAO customerDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param customerDAO the DAO used to access customer data
     */
    public CustomerRepositoryImpl(final CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * Retrieves a customer by their email.
     *
     * @param email the customer's email
     * @return an Optional containing the Customer if found, or empty
     */
    @Override
    public Optional<Customer> findByEmail(final String email) {
        return Optional.ofNullable(customerDAO.findByEmail(email));
    }

    /**
     * Checks if a customer exists with the specified email.
     *
     * @param email the email to check
     * @return true if a customer with the email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(final String email) {
        return customerDAO.existsByEmail(email);
    }

    /**
     * Retrieves all active customers.
     *
     * @return a list of active customers
     */
    @Override
    public List<Customer> findActive() {
        return customerDAO.findActive();
    }

    /**
     * Persists a new customer.
     *
     * @param customer the customer to save
     */
    @Override
    public void save(final Customer customer) {
        customerDAO.insert(customer);
    }

    /**
     * Updates an existing customer.
     *
     * @param customer the customer to update
     */
    @Override
    public void update(final Customer customer) {
        customerDAO.update(customer);
    }

    /**
     * Deletes a customer.
     *
     * @param customer the customer to delete
     */
    @Override
    public void delete(final Customer customer) {
        customerDAO.delete(customer);
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the customer's ID
     * @return an Optional containing the Customer if found, or empty
     */
    @Override
    public Optional<Customer> findById(final Integer id) {
        return customerDAO.findById(id);
    }

    /**
     * Retrieves all customers.
     *
     * @return a list of all customers
     */
    @Override
    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    /**
     * Retrieves the last 5 customers inserted.
     *
     * @return a list of the 5 most recently added customers
     */
    @Override
    public List<Customer> findLast5Inserted() {
        return customerDAO.findLast5Inserted();
    }
}
