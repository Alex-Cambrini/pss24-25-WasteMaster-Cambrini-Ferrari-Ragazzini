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

public class InvoiceManager {

    private final InvoiceDAO invoiceDAO;
    private final CollectionDAO collectionDAO;
    private static final double FIXED_FEE = 30.0;

    public InvoiceManager(InvoiceDAO invoiceDAO, CollectionDAO collectionDAO) {
        this.invoiceDAO = invoiceDAO;
        this.collectionDAO = collectionDAO;
    }

   public List<Invoice> generateInvoicesForFirstHalf(int year) {
        return generateInvoicesForPeriod(
            LocalDate.of(year, Month.JANUARY, 1),
            LocalDate.of(year, Month.JUNE, 30)
        );
    }

    public List<Invoice> generateInvoicesForSecondHalf(int year) {
        return generateInvoicesForPeriod(
            LocalDate.of(year, Month.JULY, 1),
            LocalDate.of(year, Month.DECEMBER, 31)
        );
    }

    private List<Invoice> generateInvoicesForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Collection> collections = collectionDAO.findByDateRange(startDate, endDate);

        Map<Customer, List<Collection>> customerCollectionsMap = new HashMap<>();
        for (Collection c : collections) {
            if (c.getCollectionStatus() == Collection.CollectionStatus.COMPLETED) {
                customerCollectionsMap.computeIfAbsent(c.getCustomer(), k -> new ArrayList<>()).add(c);
            }
        }

        List<Invoice> invoices = new ArrayList<>();

        
        for (Map.Entry<Customer, List<Collection>> entry : customerCollectionsMap.entrySet()) {
            List<Collection> customerCollections = entry.getValue();

            for (Collection collection : customerCollections) {
                Invoice invoice = new Invoice(collection);
                invoice.setAmount(FIXED_FEE);
                invoiceDAO.insert(invoice);
                invoices.add(invoice);
            }
        }
        return invoices;

       
    }
}