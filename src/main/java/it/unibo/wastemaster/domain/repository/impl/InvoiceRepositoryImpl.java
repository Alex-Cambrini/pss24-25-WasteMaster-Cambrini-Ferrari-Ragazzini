package it.unibo.wastemaster.domain.repository.impl;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.infrastructure.dao.InvoiceDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link InvoiceRepository} that uses {@link InvoiceDAO}
 * to perform CRUD operations on Invoice entities.
 */
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoiceDAO invoiceDAO;

    /**
     * Constructs the repository with the specified DAO.
     *
     * @param invoiceDAO the DAO used to access invoice data
     */
    public InvoiceRepositoryImpl(final InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    /**
     * Retrieves invoices within a date range.
     *
     * @param start the start date
     * @param end the end date
     * @return a list of invoices in the specified date range
     */
    @Override
    public List<Invoice> findByDateRange(final LocalDate start, final LocalDate end) {
        return invoiceDAO.findByDateRange(start, end);
    }

    /**
     * Persists a new invoice.
     *
     * @param invoice the invoice to save
     */
    @Override
    public void save(final Invoice invoice) {
        invoiceDAO.insert(invoice);
    }

    /**
     * Updates an existing invoice.
     *
     * @param invoice the invoice to update
     */
    @Override
    public void update(final Invoice invoice) {
        invoiceDAO.update(invoice);
    }

    /**
     * Deletes an invoice.
     *
     * @param invoice the invoice to delete
     */
    @Override
    public void delete(final Invoice invoice) {
        invoiceDAO.delete(invoice);
    }

    /**
     * Retrieves an invoice by its ID.
     *
     * @param id the invoice ID
     * @return an Optional containing the Invoice if found, or empty
     */
    @Override
    public Optional<Invoice> findById(final int id) {
        return invoiceDAO.findById(id);
    }

    /**
     * Retrieves all invoices.
     *
     * @return a list of all invoices
     */
    @Override
    public List<Invoice> findAll() {
        return invoiceDAO.findAll();
    }

    /**
     * Retrieves invoices associated with a specific customer.
     *
     * @param customer the customer to filter invoices
     * @return a list of invoices for the customer
     */
    @Override
    public List<Invoice> findByCustomer(final Customer customer) {
        return invoiceDAO.findByCustomer(customer);
    }

    /**
     * Retrieves the last 5 invoices related to events.
     *
     * @return a list of the 5 most recent event invoices
     */
    @Override
    public List<Invoice> findLast5InvoicesEvent() {
        return invoiceDAO.findLast5InvoicesEvent();
    }
}
