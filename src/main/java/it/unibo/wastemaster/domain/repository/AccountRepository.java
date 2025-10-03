package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Account;
import java.util.Optional;

/**
 * Repository interface for managing Account entities.
 * Provides CRUD operations and retrieval by employee email.
 */
public interface AccountRepository {

    /**
     * Retrieves an account by its unique ID.
     *
     * @param id the unique identifier of the account
     * @return an Optional containing the Account if found, or empty if not found
     */
    Optional<Account> findById(Integer id);

    /**
     * Retrieves an account associated with the specified employee email.
     *
     * @param email the email of the employee linked to the account
     * @return an Optional containing the Account if found, or empty if not found
     */
    Optional<Account> findAccountByEmployeeEmail(String email);

    /**
     * Persists a new account.
     *
     * @param account the Account entity to save
     */
    void save(Account account);

    /**
     * Updates an existing account.
     *
     * @param account the Account entity to update
     */
    void update(Account account);

    /**
     * Deletes an account.
     *
     * @param account the Account entity to delete
     */
    void delete(Account account);
}
