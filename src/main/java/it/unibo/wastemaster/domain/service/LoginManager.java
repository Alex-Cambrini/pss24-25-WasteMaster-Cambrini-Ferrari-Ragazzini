package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service for handling user authentication and login.
 */
public final class LoginManager {

    private final AccountRepository accountRepository;

    /**
     * Constructs a LoginManager with a specified AccountRepository.
     *
     * @param accountRepository the repository managing account data
     */
    public LoginManager(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Authenticates a user by verifying the provided email and raw password.
     *
     * @param email       the email address of the user
     * @param rawPassword the plain-text password to verify
     * @return an Optional containing the Account if authentication succeeds,
     *         or an empty Optional if authentication fails or account is not found
     */
    public Optional<Account> authenticate(final String email,
                                          final String rawPassword) {
        Optional<Account> accountOpt = accountRepository.findAccountByEmployeeEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(rawPassword, account.getPasswordHash())) {
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }
}
