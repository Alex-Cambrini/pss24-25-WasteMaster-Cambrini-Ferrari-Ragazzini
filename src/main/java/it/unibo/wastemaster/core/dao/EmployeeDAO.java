package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Employee;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * DAO for managing Employee entities.
 */
public final class EmployeeDAO extends GenericDAO<Employee> {

    /**
     * Constructs an EmployeeDAO with the given entity manager.
     * 
     * @param entityManager the EntityManager to use
     */
    public EmployeeDAO(final EntityManager entityManager) {
        super(entityManager, Employee.class);
    }

    /**
     * Checks whether an employee exists with the given email.
     * 
     * @param email the email to check
     * @return true if an employee exists with the email, false otherwise
     */
    public boolean existsByEmail(final String email) {
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(e) FROM Employee e WHERE e.email = :email",
                        Long.class)
                .setParameter("email", email).getSingleResult();
        return count > 0;
    }

    /**
     * Finds an employee by their email.
     * 
     * @param email the email of the employee
     * @return the Employee if found, or null otherwise
     */
    public Employee findByEmail(final String email) {
        return getEntityManager()
                .createQuery("SELECT e FROM Employee e WHERE e.email = :email",
                        Employee.class)
                .setParameter("email", email).getResultStream().findFirst().orElse(null);
    }

    /**
     * Retrieves all non-deleted employees.
     * 
     * @return list of active employees
     */
    public List<Employee> findEmployeeDetails() {
        return getEntityManager().createQuery("""
                    SELECT e FROM Employee e
                    WHERE e.isDeleted = false
                """, Employee.class).getResultList();
    }
}
