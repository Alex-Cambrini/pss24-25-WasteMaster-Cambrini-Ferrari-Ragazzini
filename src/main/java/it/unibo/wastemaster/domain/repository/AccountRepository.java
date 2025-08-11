package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Account;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Integer id);
    Optional<Account> findAccountByEmployeeEmail(String email);
    void save(Account account);
    void update(Account account);
    void delete(Account account);
}
