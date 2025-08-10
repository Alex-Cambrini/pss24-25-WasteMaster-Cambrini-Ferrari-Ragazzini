package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(Integer id);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Customer> findActiveCustomers();
    void save(Customer customer);
    void update(Customer customer);
    void delete(Customer customer);
    List<Customer> findAll();
}
