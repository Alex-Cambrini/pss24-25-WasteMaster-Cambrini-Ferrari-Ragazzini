package it.unibo.wastemaster.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Invoice.PaymentStatus;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;

/**
 * Service class responsible for managing Invoice entities, including creation,
 * retrieval, payment processing, and deletion.
 */
public class InvoiceManager {

    private static final double RECURRING_FEE = 0.25;
    private static final double ONE_TIME_FEE = 0.40;
    private final InvoiceRepository invoiceRepository;

    /**
     * Constructs an InvoiceManager with the given repository.
     *
     * @param invoiceRepository the repository used for invoice persistence
     */
    public InvoiceManager(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Creates a new invoice for the given customer based on the provided
     * collections.
     * Each recurring collection adds a fixed recurring fee, and each one-time
     * collection
     * adds a one-time fee. All included collections are marked as billed.
     *
     * @param customer          the customer for whom the invoice is created
     * @param collectionsToBill the list of collections to include in the invoice
     * @return the newly created invoice
     * @throws IllegalArgumentException if the list is null or empty
     * @throws IllegalStateException    if a collection has an unknown schedule type
     */
    public Invoice createInvoice(Customer customer, List<Collection> collectionsToBill) {
        if (collectionsToBill == null || collectionsToBill.isEmpty()) {
            throw new IllegalArgumentException("No collections to bill for this customer.");
        }

        double totalRecurring = 0.0;
        double totalOnetime = 0.0;
        int recurringCount = 0;
        int onetimeCount = 0;

        for (Collection collection : collectionsToBill) {
            if (collection.getSchedule() instanceof RecurringSchedule) {
                totalRecurring += RECURRING_FEE;
                recurringCount++;
            } else if (collection.getSchedule() instanceof OneTimeSchedule) {
                totalOnetime += ONE_TIME_FEE;
                onetimeCount++;
            } else {
                throw new IllegalStateException(
                        "Unknown schedule type for collection ID " + collection.getCollectionId());
            }

            collection.setIsBilled(true);
        }

        Invoice invoice = new Invoice(
                customer,
                collectionsToBill,
                totalRecurring,
                totalOnetime,
                recurringCount,
                onetimeCount,
                LocalDateTime.now());

        invoiceRepository.save(invoice);

        return invoice;
    }

    /**
     * Retrieves an invoice by its ID.
     *
     * @param id the ID of the invoice
     * @return an Optional containing the invoice if found, or empty if not found
     */
    public Optional<Invoice> findInvoiceById(int id) {
        return invoiceRepository.findById(id);
    }

    /**
     * Retrieves all invoices in the system.
     *
     * @return a list of all invoices
     */
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /**
     * Marks the invoice with the given ID as PAID.
     *
     * @param invoiceId the ID of the invoice to mark as paid
     * @return true if the invoice was found and updated, false otherwise
     */
    public boolean markInvoiceAsPaid(int invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            if (invoice.isDeleted()) {
                throw new IllegalStateException("Cannot modify a deleted invoice.");
            }
            invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
            invoice.setPaymentDate(LocalDateTime.now());
            invoiceRepository.update(invoice);
            return true;
        }
        return false;
    }

    /**
     * Calculates the total billed amount for a given customer across all invoices.
     *
     * @param customer the customer whose billed amount is to be calculated
     * @return the total billed amount
     */
    public double getTotalBilledAmountForCustomer(Customer customer) {
        double total = 0.0;
        for (Invoice invoice : invoiceRepository.findByCustomer(customer)) {
            total += invoice.getAmount();
        }
        return total;
    }

    /**
     * Calculates the total amount already paid by a given customer.
     *
     * @param customer the customer whose paid amount is to be calculated
     * @return the total paid amount
     */
    public double getTotalPaidAmountForCustomer(Customer customer) {
        double total = 0.0;
        for (Invoice invoice : invoiceRepository.findByCustomer(customer)) {
            if (invoice.getPaymentStatus() == Invoice.PaymentStatus.PAID) {
                total += invoice.getAmount();
            }
        }
        return total;
    }

    /**
     * Deletes the invoice with the given ID (soft delete) if it is not already
     * paid.
     * Marks the invoice as deleted and sets all its collections as not billed.
     *
     * @param invoiceId the ID of the invoice to delete
     * @return true if the invoice was found and deleted, false if the invoice
     *         was not found or is already marked as PAID
     */
    public boolean deleteInvoice(int invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            if (PaymentStatus.PAID == invoice.getPaymentStatus()) {
                return false;
            }
            invoice.setDeleted(true);
            for (Collection collection : invoice.getCollections()) {
                collection.setIsBilled(false);
            }
            invoiceRepository.update(invoice);
            return true;
        }
        return false;
    }
}
