package it.unibo.wastemaster.domain.service;

import java.util.Optional;
import it.unibo.wastemaster.core.utils.TransactionHelper;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;

/**
 * Manages operations related to employees such as creation, update, deletion and
 * permission checks.
 */
public class EmployeeManager {

    private static final String EMPLOYEE_NULL_MSG = "Employee cannot be null";
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;
    private final AccountManager accountManager;

    /**
     * Constructs an EmployeeManager with the given dependencies.
     *
     * @param employeeRepository the DAO used for employee persistence
     * @param entityManager the EntityManager used for transaction management
     * @param accountManager the manager responsible for account operations
     */
    public EmployeeManager(final EmployeeRepository employeeRepository,
            final EntityManager entityManager, final AccountManager accountManager) {
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
        this.accountManager = accountManager;
    }

    /**
     * Adds a new employee along with an associated account after validation and email
     * uniqueness check.
     *
     * @param employee the employee to add
     * @param rawPassword the plain text password for the new account
     * @return the added employee
     * @throws IllegalArgumentException if the email is already registered or inputs are
     *         invalid
     */
    public Employee addEmployee(final Employee employee, final String rawPassword) {
        ValidateUtils.requireArgNotNull(employee, EMPLOYEE_NULL_MSG);
        ValidateUtils.requireArgNotNull(rawPassword, "Password cannot be null");
        ValidateUtils.validateEntity(employee);

        if (isEmailRegistered(employee.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        TransactionHelper.executeTransaction(entityManager, () -> {
            employeeRepository.save(employee);
            accountManager.createAccount(employee, rawPassword);
        });
        return employee;
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    private boolean isEmailRegistered(final String email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * Updates an existing employee after validation and email conflict check.
     *
     * @param toUpdateEmployee the employee to update
     * @throws IllegalArgumentException if the ID is null or email is used by another
     *         employee
     */
    public void updateEmployee(final Employee toUpdateEmployee) {
        ValidateUtils.validateEntity(toUpdateEmployee);
        ValidateUtils.requireArgNotNull(toUpdateEmployee.getEmployeeId(),
                "Employee ID cannot be null");

        final Optional<Employee> existingOpt =
                employeeRepository.findByEmail(toUpdateEmployee.getEmail());
        if (existingOpt.isPresent() && !existingOpt.get().getEmployeeId()
                .equals(toUpdateEmployee.getEmployeeId())) {
            throw new IllegalArgumentException(
                    "Email is already used by another employee.");
        }
        employeeRepository.update(toUpdateEmployee);
    }


    /**
     * Performs a soft delete by marking the employee as deleted and updating it in the
     * database.
     *
     * @param employee the employee to delete
     * @return true if deletion succeeded, false otherwise
     */
    public boolean softDeleteEmployee(final Employee employee) {
        try {
            ValidateUtils.requireArgNotNull(employee, EMPLOYEE_NULL_MSG);
            ValidateUtils.requireArgNotNull(employee.getEmployeeId(),
                    "Employee ID cannot be null");
            employee.delete();
            updateEmployee(employee);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if an employee is allowed to drive a specific vehicle based on license.
     *
     * @param employee the employee
     * @param vehicle the vehicle
     * @return true if the employee can drive the vehicle, false otherwise
     */
    public boolean canDriveVehicle(final Employee employee, final Vehicle vehicle) {
        ValidateUtils.requireArgNotNull(employee, EMPLOYEE_NULL_MSG);
        ValidateUtils.requireArgNotNull(vehicle, "Vehicle cannot be null");

        return switch (employee.getLicence()) {
            case C -> true;
            case C1 -> vehicle.getRequiredLicence() == Vehicle.RequiredLicence.B
                    || vehicle.getRequiredLicence() == Vehicle.RequiredLicence.C1;
            case B -> vehicle.getRequiredLicence() == Vehicle.RequiredLicence.B;
            default -> false;
        };
    }
}
