package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.exception.AccountCreationException;
import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service class responsible for managing Account entities, including account creation and
 * password hashing.
 */
public class AccountManager {

    private final AccountRepository accountRepository;

    /**
     * Constructs an AccountManager with the given AccountDAO.
     *
     * @param accountDAO the DAO used for account persistence operations
     */
    public AccountManager(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Creates a new Account for the given Employee with the raw password. The password is
     * hashed using BCrypt before storing.
     *
     * @param employee the employee to associate with the new account
     * @param rawPassword the plain text password to hash and store
     * @return the created Account entity
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
}
