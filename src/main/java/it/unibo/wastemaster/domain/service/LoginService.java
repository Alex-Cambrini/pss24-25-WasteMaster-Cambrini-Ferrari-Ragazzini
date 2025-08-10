package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.dao.AccountDAO;
import it.unibo.wastemaster.domain.model.Account;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service for handling user authentication and login.
 */
public final class LoginService {

    private final AccountDAO accountDAO;

    /**
     * Constructs a LoginService with a specified AccountDAO.
     *
     * @param accountDAO the data access object for accounts
     */
    public LoginService(final AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Authenticates a user based on their email and raw password.
     *
     * @param email       the user's email address
     * @param rawPassword the raw password to be checked against the stored hash
     * @return an Optional containing the Account object if authentication is successful,
     * otherwise an empty Optional
     */
    public Optional<Account> authenticate(final String email,
                                          final String rawPassword) {
        Optional<Account> accountOpt = accountDAO.findAccountByEmployeeEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(rawPassword, account.getPasswordHash())) {
                return Optional.of(account); 
            }
        }
        return Optional.empty();
    }
}
