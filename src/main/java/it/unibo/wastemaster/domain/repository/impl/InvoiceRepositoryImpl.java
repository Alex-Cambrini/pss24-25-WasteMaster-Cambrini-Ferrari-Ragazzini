package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.infrastructure.dao.InvoiceDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Override
    public Optional<Invoice> findById(int id) {
        return (invoiceDAO.findById(id));
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceDAO.findAll();
    }

    @Override
    public List<Invoice> findByCustomer(Customer customer) {
        return invoiceDAO.findByCustomer(customer);
    }

    @Override
    public List<Invoice> findLast5InvoicesEvent() {
        List<Invoice> created = invoiceDAO.findLast5Created();
        List<Invoice> paid = invoiceDAO.findLast5Paid();

        List<Invoice> combined = new ArrayList<>();
        combined.addAll(created);
        combined.addAll(paid);

        combined.sort((i1, i2) -> {
            LocalDateTime d1 = i1.getPaymentDate() != null ? i1.getPaymentDate() :
                    i1.getIssueDate();
            LocalDateTime d2 = i2.getPaymentDate() != null ? i2.getPaymentDate() : i2.getIssueDate();
            return d2.compareTo(d1);
        });

        return combined.stream().limit(5).toList();
    }

}

