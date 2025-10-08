package it.unibo.wastemaster.presentationdto;

import it.unibo.wastemaster.domain.model.Invoice;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Presentation DTO class that represents an invoice row for a table view.
 * It formats and exposes invoice data as strings suitable for display.
 */
public final class InvoiceRow {

    private final Invoice invoice;
    private final String id;
    private final String customer;
    private final String amount;
    private final String status;
    private final String issueDate;
    private final String paymentDate;
    private final String serviceCounts;
    private final String totalAmounts;
    private final String isCancelled;

    /**
     * Constructs an InvoiceRow from a given {@link Invoice} object.
     * Formats invoice fields into human-readable string representations.
     *
     * @param invoice the {@link Invoice} to be represented in this row
     */
    public InvoiceRow(final Invoice invoice) {
        this.invoice = invoice;
        this.id = String.valueOf(invoice.getInvoiceId());
        this.customer = invoice.getCustomer() != null
                ? invoice.getCustomer().getName() + " " + invoice.getCustomer()
                .getSurname() : "";
        this.amount = String.format("%.2f", invoice.getAmount());
        this.status = invoice.getPaymentStatus() != null
                ? invoice.getPaymentStatus().toString() : "";
        this.issueDate =
                invoice.getIssueDate() != null ? invoice.getIssueDate().toString() : "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime paid = invoice.getPaymentDate();
        this.paymentDate = paid != null ? paid.format(fmt) : "";
        this.serviceCounts = "Recurring: " + invoice.getRecurringCount()
                + ", One-time: " + invoice.getOnetimeCount();
        this.totalAmounts =
                "Recurring: " + String.format("%.2f", invoice.getTotalRecurring())
                        + ", One-time: " + String.format("%.2f",
                        invoice.getTotalOnetime());
        this.isCancelled = invoice.isDeleted() ? "Yes" : "No";
    }

    /**
     * Returns the invoice ID as a string.
     *
     * @return invoice ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the customer's full name.
     *
     * @return customer name and surname, or empty string if customer is null
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Returns the invoice amount formatted to two decimal places.
     *
     * @return formatted invoice amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Returns the payment status of the invoice as a string.
     *
     * @return payment status, or empty string if null
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the issue date of the invoice as a string.
     *
     * @return issue date, or empty string if null
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Returns the payment date of the invoice formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return formatted payment date, or empty string if null
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Returns a summary of service counts, including recurring and one-time services.
     *
     * @return formatted service counts
     */
    public String getServiceCounts() {
        return serviceCounts;
    }

    /**
     * Returns a summary of total amounts for recurring and one-time services.
     *
     * @return formatted total amounts
     */
    public String getTotalAmounts() {
        return totalAmounts;
    }

    /**
     * Returns whether the invoice has been cancelled.
     *
     * @return "Yes" if cancelled, "No" otherwise
     */
    public String getIsCancelled() {
        return isCancelled;
    }

    /**
     * Returns the underlying {@link Invoice} object.
     *
     * @return original invoice
     */
    public Invoice getInvoice() {
        return invoice;
    }
}
