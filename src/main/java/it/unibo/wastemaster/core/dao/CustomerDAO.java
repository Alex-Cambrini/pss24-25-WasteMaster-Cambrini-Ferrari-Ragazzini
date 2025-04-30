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

    public List<Object[]> findCustomerDetails() {
        return entityManager.createQuery("""
            SELECT c.name, c.surname, c.email,
                   l.street, l.civicNumber, l.city, l.postalCode
            FROM Customer c
            JOIN c.location l
            WHERE c.isDeleted = false
        """, Object[].class).getResultList();
    }
}
