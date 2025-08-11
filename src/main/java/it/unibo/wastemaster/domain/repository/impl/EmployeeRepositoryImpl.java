package it.unibo.wastemaster.domain.repository.impl;

import jakarta.persistence.EntityManager;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.repository.EmployeeRepository;
import it.unibo.wastemaster.infrastructure.dao.EmployeeDAO;
import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeDAO employeeDAO;

    public EmployeeRepositoryImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public boolean existsByEmail(String email) {
        return employeeDAO.existsByEmail(email);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return Optional.ofNullable(employeeDAO.findByEmail(email));
    }

    @Override
    public List<Employee> findAllActive() {
        return employeeDAO.findEmployeeDetails();
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        return employeeDAO.findById(id);
    }

    @Override
    public void save(Employee employee) {
        employeeDAO.insert(employee);
    }

    @Override
    public void update(Employee employee) {
        employeeDAO.update(employee);
    }

    @Override
    public void delete(Employee employee) {
        employeeDAO.delete(employee);
    }
}
