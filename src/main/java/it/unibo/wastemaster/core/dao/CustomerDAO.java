package it.unibo.wastemaster.core.dao;

import java.util.List;

import it.unibo.wastemaster.core.models.Customer;
import jakarta.persistence.EntityManager;

public class CustomerDAO extends GenericDAO<Customer> {

    public CustomerDAO(EntityManager entityManager) {
        super(entityManager, Customer.class);
    }

    public boolean existsByEmail(String email) {
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public Customer findByEmail(String email) {
        return entityManager.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Customer> findCustomerDetails() {
        return entityManager.createQuery("""
                	SELECT c FROM Customer c
                	WHERE c.isDeleted = false
                """, Customer.class).getResultList();
    }
}
