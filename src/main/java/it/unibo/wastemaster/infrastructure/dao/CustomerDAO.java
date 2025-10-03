package it.unibo.wastemaster.infrastructure.dao;

import it.unibo.wastemaster.domain.model.Customer;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * DAO for managing Customer entities.
 */
public final class CustomerDAO extends GenericDAO<Customer> {

    /**
     * Maximum number of recent customers to retrieve.
     */
    private static final int LAST_INSERTED_LIMIT = 5;

    /**
     * Constructs a CustomerDAO with the given entity manager.
     *
     * @param entityManager the EntityManager to use
     */
    public CustomerDAO(final EntityManager entityManager) {
        super(entityManager, Customer.class);
    }

    /**
     * Checks whether a customer exists with the given email.
     *
     * @param email the email to check
     * @return true if a customer exists with the email, false otherwise
     */
    public boolean existsByEmail(final String email) {
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email",
                        Long.class)
                .setParameter("email", email).getSingleResult();
        return count > 0;
    }

    /**
     * Finds a customer by their email.
     *
     * @param email the email of the customer
     * @return the Customer if found, or null otherwise
     */
    public Customer findByEmail(final String email) {
        return getEntityManager()
                .createQuery("SELECT c FROM Customer c WHERE c.email = :email",
                        Customer.class)
                .setParameter("email", email).getResultStream().findFirst().orElse(null);
    }

    /**
     * Retrieves all non-deleted customers.
     *
     * @return list of active customers
     */
    public List<Customer> findActive() {
        return getEntityManager().createQuery("""
                    SELECT c FROM Customer c
                    WHERE c.isDeleted = false
                """, Customer.class).getResultList();
    }

    /**
     * Retrieves the last 5 inserted customers, ordered by creation date
     * (descending).
     *
     * @return a list of the last 5 inserted customers
     */
    public List<Customer> findLast5Inserted() {
        return getEntityManager()
                .createQuery("SELECT c FROM Customer c ORDER BY c.createdDate DESC",
                        Customer.class)
                .setMaxResults(LAST_INSERTED_LIMIT)
                .getResultList();
    }
}
