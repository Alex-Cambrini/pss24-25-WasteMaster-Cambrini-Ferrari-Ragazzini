package it.unibo.wastemaster.domain.repository;

import it.unibo.wastemaster.domain.model.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Invoice entities.
 * Provides CRUD operations and retrieval methods by date, customer, and recent events.
 */
public interface InvoiceRepository {

    /**
     * Retrieves all invoices within the specified date range.
     *
     * @param start the start date of the range (inclusive)
     * @param end the end date of the range (inclusive)
     * @return a list of Invoice entities within the date range
     */
    List<Invoice> findByDateRange(LocalDate start, LocalDate end);

    /**
     * Persists a new invoice.
     *
     * @param invoice the Invoice entity to save
     */
    void save(Invoice invoice);

    /**
     * Updates an existing invoice.
     *
     * @param invoice the Invoice entity to update
     */
    void update(Invoice invoice);

    /**
     * Deletes an invoice.
     *
     * @param invoice the Invoice entity to delete
     */
    void delete(Invoice invoice);

    /**
     * Retrieves an invoice by its unique ID.
     *
     * @param id the unique identifier of the invoice
     * @return an Optional containing the Invoice if found, or empty if not found
     */
    Optional<Invoice> findById(int id);

    /**
     * Retrieves all invoices.
     *
     * @return a list of all Invoice entities
     */
    List<Invoice> findAll();

    /**
     * Retrieves all invoices associated with the specified customer.
     *
     * @param customer the Customer entity to filter invoices
     * @return a list of Invoice entities for the given customer
     */
    List<Invoice> findByCustomer(it.unibo.wastemaster.domain.model.Customer customer);

    /**
     * Retrieves the last 5 invoice events for notification purposes.
     *
     * @return a list of the 5 most recent Invoice entities
     */
    List<Invoice> findLast5InvoicesEvent();
}
