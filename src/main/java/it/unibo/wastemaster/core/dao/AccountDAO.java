package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.models.Account;
import jakarta.persistence.EntityManager;

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
}
