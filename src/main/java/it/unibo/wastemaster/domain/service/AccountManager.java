package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.exception.AccountCreationException;
import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service class responsible for managing Account entities, including account
 * creation and
 * password hashing.
 */
public class AccountManager {

    private final AccountRepository accountRepository;

    /**
     * Constructs an AccountManager with the given AccountRepository.
     *
     * @param accountRepository the repository used for account persistence
     *                          operations
     */
    public AccountManager(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Creates a new Account for the given Employee using the provided raw password.
     * The password is validated (at least 8 characters, at least one letter and one
     * number)
     * and then hashed with BCrypt before being stored.
     *
     * @param employee    the employee to associate with the new account (must not
     *                    be null)
     * @param rawPassword the plain text password to validate and hash (must not be
     *                    null/blank)
     * @return the created Account entity
     * @throws IllegalArgumentException if the password is null/blank or does not
     *                                  satisfy
     *                                  the minimum policy (>= 8 chars, >= 1 letter,
     *                                  >= 1 digit)
     * @throws AccountCreationException if the account cannot be persisted
     */
    public Account createAccount(final Employee employee, final String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be blank.");
        }

        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        if (!rawPassword.matches(passwordPattern)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include at least "
                            + "one letter and one number.");
        }
        String passwordHash = hashPassword(rawPassword);
        Account newAccount = new Account(passwordHash, employee);

        try {
            accountRepository.save(newAccount);
            return newAccount;
        } catch (Exception e) {
            throw new AccountCreationException("Failed to create account", e);
        }
    }

    /**
     * Hashes the raw password using BCrypt.
     *
     * @param rawPassword the plain text password
     * @return the hashed password
     */
    private String hashPassword(final String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * Retrieves an Account associated with the Employee identified by the given
     * email.
     *
     * @param email the email of the employee whose account is to be retrieved
     * @return an Optional containing the associated Account if found, or an empty
     *         Optional otherwise
     */
    public Optional<Account> findAccountByEmployeeEmail(final String email) {
        return accountRepository.findAccountByEmployeeEmail(email);
    }
}
