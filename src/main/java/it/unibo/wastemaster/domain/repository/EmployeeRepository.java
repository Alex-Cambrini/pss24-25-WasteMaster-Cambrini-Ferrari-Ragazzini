package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Employee entities.
 * Provides CRUD operations and retrieval methods by email and active status.
 */
public interface EmployeeRepository {

    /**
     * Retrieves an employee by their unique ID.
     *
     * @param id the unique identifier of the employee
     * @return an Optional containing the Employee if found, or empty if not found
     */
    Optional<Employee> findById(Integer id);

    /**
     * Persists a new employee.
     *
     * @param employee the Employee entity to save
     */
    void save(Employee employee);

    /**
     * Updates an existing employee.
     *
     * @param employee the Employee entity to update
     */
    void update(Employee employee);

    /**
     * Deletes an employee.
     *
     * @param employee the Employee entity to delete
     */
    void delete(Employee employee);

    /**
     * Checks if an employee with the given email already exists.
     *
     * @param email the email to check
     * @return true if an employee with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves an employee by their email address.
     *
     * @param email the email of the employee
     * @return an Optional containing the Employee if found, or empty if not found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Retrieves all active (not deleted) employees.
     *
     * @return a list of active Employee entities
     */
    List<Employee> findAllActive();
}
