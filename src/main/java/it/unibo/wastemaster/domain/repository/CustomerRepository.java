package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Customer entities.
 * Provides CRUD operations and retrieval methods by email, status, and insertion order.
 */
public interface CustomerRepository {

    /**
     * Retrieves a customer by their unique ID.
     *
     * @param id the unique identifier of the customer
     * @return an Optional containing the Customer if found, or empty if not found
     */
    Optional<Customer> findById(Integer id);

    /**
     * Retrieves a customer by their email address.
     *
     * @param email the email of the customer
     * @return an Optional containing the Customer if found, or empty if not found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Checks if a customer with the given email already exists.
     *
     * @param email the email to check
     * @return true if a customer with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all active (not deleted) customers.
     *
     * @return a list of active Customer entities
     */
    List<Customer> findActive();

    /**
     * Persists a new customer.
     *
     * @param customer the Customer entity to save
     */
    void save(Customer customer);

    /**
     * Updates an existing customer.
     *
     * @param customer the Customer entity to update
     */
    void update(Customer customer);

    /**
     * Deletes a customer.
     *
     * @param customer the Customer entity to delete
     */
    void delete(Customer customer);

    /**
     * Retrieves all customers.
     *
     * @return a list of all Customer entities
     */
    List<Customer> findAll();

    /**
     * Retrieves the last 5 customers inserted.
     *
     * @return a list of the 5 most recently inserted Customer entities
     */
    List<Customer> findLast5Inserted();
}
