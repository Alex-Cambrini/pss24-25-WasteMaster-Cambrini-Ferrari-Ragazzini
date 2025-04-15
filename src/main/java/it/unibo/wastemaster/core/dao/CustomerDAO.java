package it.unibo.wastemaster.core.dao;

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

}
