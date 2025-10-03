package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import it.unibo.wastemaster.infrastructure.dao.AccountDAO;
import java.util.Optional;

/**
 * Implementation of {@link AccountRepository} that uses {@link AccountDAO}
 * to perform CRUD operations on Account entities.
 */
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountDAO accountDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param accountDAO the DAO used to access account data
     */
    public AccountRepositoryImpl(final AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Retrieves an account by its unique ID.
     *
     * @param id the unique identifier of the account
     * @return an Optional containing the Account if found, or empty if not found
     */
    @Override
    public Optional<Account> findById(final Integer id) {
        return accountDAO.findById(id);
    }

    /**
     * Persists a new account.
     *
     * @param account the Account entity to save
     */
    @Override
    public void save(final Account account) {
        accountDAO.insert(account);
    }

    /**
     * Deletes an existing account.
     *
     * @param account the Account entity to delete
     */
    @Override
    public void delete(final Account account) {
        accountDAO.delete(account);
    }

    /**
     * Retrieves an account associated with the specified employee email.
     *
     * @param email the email of the employee linked to the account
     * @return an Optional containing the Account if found, or empty if not found
     */
    @Override
    public Optional<Account> findAccountByEmployeeEmail(final String email) {
        return accountDAO.findAccountByEmployeeEmail(email);
    }

    /**
     * Updates an existing account.
     *
     * @param account the Account entity to update
     */
    @Override
    public void update(final Account account) {
        accountDAO.update(account);
    }
}
