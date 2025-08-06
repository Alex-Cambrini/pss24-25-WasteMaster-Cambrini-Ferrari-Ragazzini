package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.InvoiceDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Invoice;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the creation and generation of invoices for waste collections.
 */
public final class InvoiceManager {

    private static final double FIXED_FEE = 30.0;
    private static final int JUNE_LAST_DAY = 30;
    private static final int DECEMBER_LAST_DAY = 31;

    private final InvoiceDAO invoiceDAO;
    private final CollectionDAO collectionDAO;

    /**
     * Constructs an InvoiceManager with a specified InvoiceDAO and CollectionDAO.
     *
     * @param invoiceDAO    the data access object for invoices
     * @param collectionDAO the data access object for collections
     */
    public InvoiceManager(final InvoiceDAO invoiceDAO,
                          final CollectionDAO collectionDAO) {
        this.invoiceDAO = invoiceDAO;
        this.collectionDAO = collectionDAO;
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
     * @param endDate   the end date of the period (inclusive)
     * @return a list of generated invoices
     */
    private List<Invoice> generateInvoicesForPeriod(final LocalDate startDate,
                                                    final LocalDate endDate) {
        List<Collection> collections = collectionDAO.findByDateRange(startDate, endDate);

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
                invoiceDAO.insert(invoice);
                invoices.add(invoice);
            }
        }
        return invoices;
    }
}
