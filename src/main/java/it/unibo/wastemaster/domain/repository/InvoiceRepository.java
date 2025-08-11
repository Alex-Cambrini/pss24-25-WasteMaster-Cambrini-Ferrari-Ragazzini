package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Invoice;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository {
    List<Invoice> findByDateRange(LocalDate start, LocalDate end);
    void save(Invoice invoice);
    void update(Invoice invoice);
    void delete(Invoice invoice);
}
