package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.AccountDAO;
import it.unibo.wastemaster.core.models.Account;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private final AccountDAO accountDAO;

    public LoginService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Optional<Account> authenticate(String email, String rawPassword) {
        Optional<Account> accountOpt = accountDAO.findAccountByEmployeeEmail(email);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (BCrypt.checkpw(rawPassword, account.getPasswordHash())) {
                return Optional.of(account); // Qui c'è accesso a employee → ruolo
            }
        }
        return Optional.empty();
    }
}
