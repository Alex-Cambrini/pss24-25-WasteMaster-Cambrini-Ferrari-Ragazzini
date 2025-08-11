package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(Integer id);
    void save(Employee employee);
    void update(Employee employee);
    void delete(Employee employee);
    boolean existsByEmail(String email);
    Optional<Employee> findByEmail(String email);
    List<Employee> findAllActive();
}