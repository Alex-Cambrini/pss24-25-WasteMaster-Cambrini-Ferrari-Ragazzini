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

public class InvoiceManager {

    private static final double RECURRING_FEE = 0.25;
    private static final double ONE_TIME_FEE = 0.40;
    private final InvoiceRepository invoiceRepository;

    public InvoiceManager(InvoiceRepository invoiceRepository){
        this.invoiceRepository = invoiceRepository;
    }

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

    public Optional<Invoice> findInvoiceById(int id) {
    return invoiceRepository.findById(id);
    }

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
            LocalDateTime now = LocalDateTime.now();
            invoice.setPaymentDate(now);
            invoice.setLastModified(now);
            invoiceRepository.update(invoice);
            return true;
        }
        return false;
    }


    public double getTotalBilledAmountForCustomer(Customer customer) {
    double total = 0.0;
    for (Invoice invoice : invoiceRepository.findByCustomer(customer)) {
        total += invoice.getAmount();
    }
    return total;
    }

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
     * Deletes the invoice with the given ID (soft delete) if it is not already paid.
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
