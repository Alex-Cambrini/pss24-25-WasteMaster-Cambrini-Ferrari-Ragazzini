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

    public boolean authenticate(String email, String rawPassword) {
        Optional<Account> accountOpt = accountDAO.findAccountByEmployeeEmail(email);
        if (accountOpt.isEmpty()) {
            return false;
        }

        Account account = accountOpt.get();
        return BCrypt.checkpw(rawPassword, account.getPasswordHash());
    }
}
