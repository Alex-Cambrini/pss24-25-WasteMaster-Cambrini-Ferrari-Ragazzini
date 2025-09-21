package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;


/**
 * ViewModel class that represents an invoice for the table view.
 */
public final class InvoiceRow {

    private final Invoice invoice;
    private final String id;
    private final String customer;
    private final String amount;
    private final String status;

    public InvoiceRow(final Invoice invoice) {
        this.invoice = invoice;
        this.id = String.valueOf(invoice.getInvoiceId());
        this.customer = invoice.getCustomer() != null ? invoice.getCustomer().getName() : "";
        this.amount = String.format("%.2f", invoice.getAmount());
        this.status = invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().toString() : "";
    }

     public Invoice getInvoice() {
        return invoice;
    }

    public String getId() {
        return id;
    }

    public int getIdAsInt() {
        return invoice.getInvoiceId();
    }

    public String getCustomer() {
        return customer;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Customer getCustomerObject() {
    return invoice.getCustomer();
}
}