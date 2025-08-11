package it.unibo.wastemaster.domain.repository.impl;

import java.util.Optional;
import it.unibo.wastemaster.domain.model.Account;
import it.unibo.wastemaster.domain.repository.AccountRepository;
import it.unibo.wastemaster.infrastructure.dao.AccountDAO;

public class AccountRepositoryImpl implements AccountRepository {

    private final AccountDAO accountDAO;

    public AccountRepositoryImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Optional<Account> findById(Integer id) {
        return accountDAO.findById(id);
    }

    @Override
    public void save(Account account) {
        accountDAO.insert(account);
    }

    @Override
    public void delete(Account account) {
        accountDAO.delete(account);
    }

    @Override
    public Optional<Account> findAccountByEmployeeEmail(String email) {
        return accountDAO.findAccountByEmployeeEmail(email);        
    }

    @Override
    public void update(Account account) {
       accountDAO.update(account); 
    }
}
