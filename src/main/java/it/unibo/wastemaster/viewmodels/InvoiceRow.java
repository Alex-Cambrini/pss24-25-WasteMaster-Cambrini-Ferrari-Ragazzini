package it.unibo.wastemaster.viewmodels;

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
    private final String issueDate;
    private final String serviceCounts;
    private final String totalAmounts;
    private final String isCancelled;

    public InvoiceRow(Invoice invoice) {
        this.invoice = invoice;
        this.id = String.valueOf(invoice.getInvoiceId());
        this.customer = invoice.getCustomer() != null ?
                invoice.getCustomer().getName() + " " + invoice.getCustomer().getSurname() : "";
        this.amount = String.format("%.2f", invoice.getAmount());
        this.status = invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().toString() : "";
        this.issueDate = invoice.getIssueDate() != null ? invoice.getIssueDate().toString() : "";
        this.serviceCounts = "Recurring: " + invoice.getRecurringCount()
                + ", One-time: " + invoice.getOnetimeCount();
        this.totalAmounts = "Recurring: " + String.format("%.2f", invoice.getTotalRecurring())
                + ", One-time: " + String.format("%.2f", invoice.getTotalOnetime());
        this.isCancelled = invoice.isDeleted() ? "Yes" : "No";
    }

    public String getId() { return id; }
    public String getCustomer() { return customer; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getIssueDate() { return issueDate; }
    public String getServiceCounts() { return serviceCounts; }
    public String getTotalAmounts() { return totalAmounts; }
    public String getIsCancelled() { return isCancelled; }

    public Invoice getInvoice() { return invoice; }
}
