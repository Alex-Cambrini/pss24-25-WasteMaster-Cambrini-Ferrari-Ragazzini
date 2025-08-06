package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.Optional;

/**
 * DAO for {@link Account} entity operations.
 */
public class AccountDAO extends GenericDAO<Account> {

    /**
     * Constructor that initializes the DAO with the provided entity manager.
     *
     * @param entityManager the entity manager instance
     */
    public AccountDAO(final EntityManager entityManager) {
        super(entityManager, Account.class);
    }

    /**
     * Finds an Account associated with the Employee who has the specified email.
     *
     * @param email the email of the Employee
     * @return an Optional containing the Account if found, or empty if no result
     */
    public Optional<Account> findAccountByEmployeeEmail(final String email) {
        try {
            Account account = getEntityManager().createQuery(
                            "SELECT a FROM Account a JOIN a.employee e WHERE e.email = "
                                    + ":email",
                            Account.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
