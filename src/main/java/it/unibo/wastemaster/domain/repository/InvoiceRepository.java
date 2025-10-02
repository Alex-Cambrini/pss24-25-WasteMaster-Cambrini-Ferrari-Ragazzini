package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {

    List<Invoice> findByDateRange(LocalDate start, LocalDate end);

    void save(Invoice invoice);

    void update(Invoice invoice);

    void delete(Invoice invoice);

    Optional<Invoice> findById(int id);

    List<Invoice> findAll();

    List<Invoice> findByCustomer(it.unibo.wastemaster.domain.model.Customer customer);

    List<Invoice> findLast5InvoicesEvent();

}
