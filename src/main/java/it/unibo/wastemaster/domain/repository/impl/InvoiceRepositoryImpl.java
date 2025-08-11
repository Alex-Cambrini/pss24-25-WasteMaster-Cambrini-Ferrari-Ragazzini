package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.infrastructure.dao.InvoiceDAO;
import java.time.LocalDate;
import java.util.List;

public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoiceDAO invoiceDAO;

    public InvoiceRepositoryImpl(final InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    @Override
    public List<Invoice> findByDateRange(LocalDate start, LocalDate end) {
        return invoiceDAO.findByDateRange(start, end);
    }

    @Override
    public void save(Invoice invoice) {
        invoiceDAO.insert(invoice);
    }

    @Override
    public void update(Invoice invoice) {
        invoiceDAO.update(invoice);
    }

    @Override
    public void delete(Invoice invoice) {
        invoiceDAO.delete(invoice);
    }
}

