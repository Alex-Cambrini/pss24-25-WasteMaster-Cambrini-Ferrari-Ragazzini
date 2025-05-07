package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Employee;
import jakarta.persistence.EntityManager;

public class EmployeeDAO extends GenericDAO<Employee> {

        public EmployeeDAO(EntityManager entityManager) {
        super(entityManager, Employee.class);
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager
        .createQuery("SELECT COUNT(e) FROM Employee e WHERE e.email = :email", Long.class)
        .setParameter("email", email)
        .getSingleResult();
        return count > 0;
    }

    public Employee findByEmail(String email) {
        return entityManager.createQuery(
            "SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
            .setParameter("email", email)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }
}
