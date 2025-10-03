package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.repository.EmployeeRepository;
import it.unibo.wastemaster.infrastructure.dao.EmployeeDAO;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link EmployeeRepository} that uses {@link EmployeeDAO}
 * to perform CRUD operations on Employee entities.
 */
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeDAO employeeDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param employeeDAO the DAO used to access employee data
     */
    public EmployeeRepositoryImpl(final EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    /**
     * Checks if an employee exists with the specified email.
     *
     * @param email the email to check
     * @return true if an employee with the email exists, false otherwise
     */
    @Override
    public boolean existsByEmail(final String email) {
        return employeeDAO.existsByEmail(email);
    }

    /**
     * Retrieves an employee by their email.
     *
     * @param email the employee's email
     * @return an Optional containing the Employee if found, or empty
     */
    @Override
    public Optional<Employee> findByEmail(final String email) {
        return employeeDAO.findByEmail(email);
    }

    /**
     * Retrieves all active employees.
     *
     * @return a list of active employees
     */
    @Override
    public List<Employee> findAllActive() {
        return employeeDAO.findActiveEmployees();
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the employee's ID
     * @return an Optional containing the Employee if found, or empty
     */
    @Override
    public Optional<Employee> findById(final Integer id) {
        return employeeDAO.findById(id);
    }

    /**
     * Persists a new employee.
     *
     * @param employee the employee to save
     */
    @Override
    public void save(final Employee employee) {
        employeeDAO.insert(employee);
    }

    /**
     * Updates an existing employee.
     *
     * @param employee the employee to update
     */
    @Override
    public void update(final Employee employee) {
        employeeDAO.update(employee);
    }

    /**
     * Deletes an employee.
     *
     * @param employee the employee to delete
     */
    @Override
    public void delete(final Employee employee) {
        employeeDAO.delete(employee);
    }
}
