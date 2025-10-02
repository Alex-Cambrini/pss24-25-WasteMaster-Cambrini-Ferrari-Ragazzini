package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.infrastructure.dao.CustomerDAO;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerDAO customerDAO;

    public CustomerRepositoryImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return Optional.ofNullable(customerDAO.findByEmail(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerDAO.existsByEmail(email);
    }

    @Override
    public List<Customer> findActive() {
        return customerDAO.findActive();
    }

    @Override
    public void save(Customer customer) {
        customerDAO.insert(customer);
    }

    @Override
    public void update(Customer customer) {
        customerDAO.update(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerDAO.delete(customer);
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        return customerDAO.findById(id);
    }

    @Override
    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    @Override
    public List<Customer> findLast5Inserted() {
        return customerDAO.findLast5Inserted();
    }
}
