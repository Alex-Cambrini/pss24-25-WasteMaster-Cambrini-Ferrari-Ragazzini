package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.EmployeeDAO;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.utils.ValidateUtils;

public class EmployeeManager {

    private EmployeeDAO employeeDAO;

    public EmployeeManager(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public Employee addEmployee(Employee employee) {
        if (isEmailRegistered(employee.getEmail())) {
            throw new IllegalArgumentException(
                    String.format("Cannot add employee: the email address '%s' is already in use.",
                            employee.getEmail()));
        }
        employeeDAO.insert(employee);
        return employee;
    }

    private boolean isEmailRegistered(String email) {
        return employeeDAO.existsByEmail(email);
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.findById(employeeId);
    }

    public void updateEmployee(Employee toUpdateEmployee) {
        ValidateUtils.validateEntity(toUpdateEmployee);
        ValidateUtils.requireArgNotNull(toUpdateEmployee.getEmployeeId(), "Employee ID cannot be null");
        Employee existing = employeeDAO.findByEmail(toUpdateEmployee.getEmail());
        if (existing != null && !existing.getEmployeeId().equals(toUpdateEmployee.getEmployeeId())) {
            throw new IllegalArgumentException("Email is already used by another employee.");
        }
        employeeDAO.update(toUpdateEmployee);
    }

    public boolean softDeleteEmployee(Employee employee) {
        try {
            ValidateUtils.requireArgNotNull(employee, "Employee cannot be null");
            ValidateUtils.requireArgNotNull(employee.getEmployeeId(), "Employee ID cannot be null");
            employee.delete();
            updateEmployee(employee);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}