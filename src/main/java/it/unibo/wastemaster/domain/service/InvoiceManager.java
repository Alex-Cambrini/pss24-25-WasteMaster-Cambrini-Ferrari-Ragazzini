package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.repository.CollectionRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.model.Invoice.PaymentStatus;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the creation and generation of invoices for waste collections.
 */
public final class InvoiceManager {

    private static final double FIXED_FEE = 30.0;
    private static final int JUNE_LAST_DAY = 30;
    private static final int DECEMBER_LAST_DAY = 31;

    private final InvoiceRepository invoiceRepository;
    private final CollectionRepository collectionRepository;

    /**
     * Constructs an InvoiceManager with specified repositories for invoices and
     * collections.
     *
     * @param invoiceRepository the repository managing invoice data
     * @param collectionRepository the repository managing collection data
     */
    public InvoiceManager(final InvoiceRepository invoiceRepository,
                          final CollectionRepository collectionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * Generates invoices for the first half of a given year (January 1st - June 30th).
     *
     * @param year the year for which to generate invoices
     * @return a list of generated invoices
     */
    public List<Invoice> generateInvoicesForFirstHalf(final int year) {
        return generateInvoicesForPeriod(
                LocalDate.of(year, Month.JANUARY, 1),
                LocalDate.of(year, Month.JUNE, JUNE_LAST_DAY)
        );
    }

    /**
     * Generates invoices for the second half of a given year (July 1st - December 31st).
     *
     * @param year the year for which to generate invoices
     * @return a list of generated invoices
     */
    public List<Invoice> generateInvoicesForSecondHalf(final int year) {
        return generateInvoicesForPeriod(
                LocalDate.of(year, Month.JULY, 1),
                LocalDate.of(year, Month.DECEMBER, DECEMBER_LAST_DAY)
        );
    }

    /**
     * Generates invoices for a specific date range.
     *
     * @param startDate the start date of the period (inclusive)
     * @param endDate the end date of the period (inclusive)
     * @return a list of generated invoices
     */
    private List<Invoice> generateInvoicesForPeriod(final LocalDate startDate,
                                                    final LocalDate endDate) {
        List<Collection> collections =
                collectionRepository.findByDateRange(startDate, endDate);

        Map<Customer, List<Collection>> customerCollectionsMap = new HashMap<>();
        for (final Collection c : collections) {
            if (c.getCollectionStatus() == Collection.CollectionStatus.COMPLETED) {
                customerCollectionsMap.computeIfAbsent(c.getCustomer(),
                        k -> new ArrayList<>()).add(c);
            }
        }

        List<Invoice> invoices = new ArrayList<>();

        for (final Map.Entry<Customer,
                List<Collection>> entry : customerCollectionsMap.entrySet()) {
            List<Collection> customerCollections = entry.getValue();

            for (final Collection collection : customerCollections) {
                Invoice invoice = new Invoice(collection);
                invoice.setAmount(FIXED_FEE);
                invoiceRepository.save(invoice);
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    /**
     * Deletes an invoice by its ID.
     *
     * @param id the invoice ID
     * @return true if the invoice was deleted, false otherwise
     */
    public boolean deleteInvoice(int id) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(id);
        if (invoiceOpt.isPresent()) {
            invoiceRepository.delete(invoiceOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Updates an existing invoice.
     *
     * @param invoiceId the invoice ID
     * @param collection the new collection
     * @param amount the new amount
     * @param status the new payment status
     */
    public void updateInvoice(int invoiceId, Collection collection, double amount, PaymentStatus status) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById((int) invoiceId);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setCollection(collection);
            invoice.setAmount(amount);
            invoice.setPaymentStatus(status);
            invoiceRepository.save(invoice);
        }
    }

    /**
     * Returns all collections.
     *
     * @return a list of all collections
     */
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    /**
     * Creates a new invoice.
     *
     * @param collection the associated collection
     * @param amount the invoice amount
     * @param status the payment status
     */
    public void createInvoice(Collection collection, double amount, PaymentStatus status) {
        Invoice invoice = new Invoice(collection);
        invoice.setAmount(amount);
        invoice.setPaymentStatus(status);
        invoiceRepository.save(invoice);
    }
}
