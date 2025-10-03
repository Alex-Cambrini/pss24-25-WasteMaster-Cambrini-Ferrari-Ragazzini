package it.unibo.wastemaster.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an invoice issued to a customer, including associated collections,
 * total amounts, payment status, and tracking fields for creation and modifications.
 * Maps to the "invoices" table in the database.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    /**
     * Unique identifier for the invoice, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    /**
     * List of collections associated with this invoice.
     */
    @OneToMany
    @JoinColumn(name = "invoice_id")
    private List<Collection> collections = new ArrayList<>();

    /**
     * Customer associated with this invoice. Cannot be null.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "The customer cannot be null")
    private Customer customer;

    /**
     * Date when the invoice was issued. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "The issue date cannot be null")
    private LocalDateTime issueDate;

    /**
     * Date when the invoice was paid. Null if not yet paid.
     */
    @Column
    private LocalDateTime paymentDate;

    /**
     * Current payment status of the invoice. Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "The payment status cannot be null")
    private PaymentStatus paymentStatus;

    /**
     * Timestamp of the last modification to this invoice. Cannot be null.
     */
    @Column(nullable = false)
    @NotNull(message = "The last modified date cannot be null")
    private LocalDateTime lastModified;

    /**
     * Total amount of the invoice (sum of recurring and one-time totals).
     */
    @Column(nullable = false)
    private double amount;

    /**
     * Total amount from recurring collections included in this invoice.
     */
    @Column(nullable = false)
    private double totalRecurring;

    /**
     * Total amount from one-time collections included in this invoice.
     */
    @Column(nullable = false)
    private double totalOnetime;

    /**
     * Number of recurring collections included in this invoice.
     */
    @Column(nullable = false)
    private int recurringCount;

    /**
     * Number of one-time collections included in this invoice.
     */
    @Column(nullable = false)
    private int onetimeCount;

    /**
     * Flag indicating whether the invoice has been deleted (soft delete).
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * Instantiates a new Invoice.
     */
    public Invoice() {
        // required by Hibernate
    }

    /**
     * Instantiates a new Invoice.
     *
     * @param customer the customer
     * @param collections the collections
     * @param totalRecurring the total recurring
     * @param totalOnetime the total onetime
     * @param recurringCount the recurring count
     * @param onetimeCount the onetime count
     * @param issueDate the issue date
     */
    public Invoice(final Customer customer, final List<Collection> collections,
                   final double totalRecurring, final double totalOnetime,
                   final int recurringCount, final int onetimeCount,
                   final LocalDateTime issueDate) {
        this.customer = customer;
        this.collections = new ArrayList<>(collections);
        this.totalRecurring = totalRecurring;
        this.totalOnetime = totalOnetime;
        this.recurringCount = recurringCount;
        this.onetimeCount = onetimeCount;
        this.issueDate = issueDate;
        this.paymentStatus = PaymentStatus.UNPAID;
        this.amount = totalRecurring + totalOnetime;
        this.lastModified = issueDate;
    }

    /**
     * Gets invoice id.
     *
     * @return the invoice id
     */
    public Integer getInvoiceId() {
        return invoiceId;
    }

    /**
     * Gets collections.
     *
     * @return the collections
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Sets collections.
     *
     * @param collections the collections
     */
    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * Gets customer.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets customer.
     *
     * @param customer the customer
     */
    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets issue date.
     *
     * @return the issue date
     */
    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    /**
     * Sets issue date.
     *
     * @param issueDate the issue date
     */
    public void setIssueDate(final LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Gets payment status.
     *
     * @return the payment status
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets payment status.
     *
     * @param paymentStatus the payment status
     */
    public void setPaymentStatus(final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(final double amount) {
        this.amount = amount;
    }

    /**
     * Gets total recurring.
     *
     * @return the total recurring
     */
    public double getTotalRecurring() {
        return totalRecurring;
    }

    /**
     * Sets total recurring.
     *
     * @param totalRecurring the total recurring
     */
    public void setTotalRecurring(final double totalRecurring) {
        this.totalRecurring = totalRecurring;
    }

    /**
     * Gets total onetime.
     *
     * @return the total onetime
     */
    public double getTotalOnetime() {
        return totalOnetime;
    }

    /**
     * Sets total onetime.
     *
     * @param totalOnetime the total onetime
     */
    public void setTotalOnetime(final double totalOnetime) {
        this.totalOnetime = totalOnetime;
    }

    /**
     * Gets payment date.
     *
     * @return the payment date
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets payment date.
     *
     * @param paymentDate the payment date
     */
    public void setPaymentDate(final LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets recurring count.
     *
     * @return the recurring count
     */
    public int getRecurringCount() {
        return recurringCount;
    }

    /**
     * Sets recurring count.
     *
     * @param recurringCount the recurring count
     */
    public void setRecurringCount(final int recurringCount) {
        this.recurringCount = recurringCount;
    }

    /**
     * Gets onetime count.
     *
     * @return the onetime count
     */
    public int getOnetimeCount() {
        return onetimeCount;
    }

    /**
     * Sets onetime count.
     *
     * @param onetimeCount the onetime count
     */
    public void setOnetimeCount(final int onetimeCount) {
        this.onetimeCount = onetimeCount;
    }

    /**
     * Checks whether the invoice is marked as deleted (soft delete).
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Sets deleted.
     *
     * @param deleted the deleted
     */
    public void setDeleted(final boolean deleted) {
        this.isDeleted = deleted;
    }

    /**
     * Gets last modified.
     *
     * @return the last modified
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Sets last modified.
     *
     * @param lastModified the last modified
     */
    public void setLastModified(final LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Returns a string representation of the invoice, including key details:
     * invoice ID, customer name, total amount, payment status, issue date,
     * and the number of collections associated with this invoice.
     * Null values are replaced with "null" and the amount is formatted to two decimal
     * places.
     *
     * @return formatted string describing the invoice
     */
    @Override
    public String toString() {
        return "Invoice{id=" + invoiceId
                + ", customer=" + (customer != null ? customer.getName() : "null")
                + ", amount=" + String.format("%.2f", amount)
                + ", status=" + (paymentStatus != null ? paymentStatus.name() : "null")
                + ", date=" + (issueDate != null ? issueDate.toString() : "null")
                + ", collections=" + (collections != null ? collections.size() : 0)
                + "}";
    }

    /**
     * The enum Payment status.
     */
    public enum PaymentStatus {
        /**
         * Paid payment status.
         */
        PAID,
        /**
         * Unpaid payment status.
         */
        UNPAID
    }
}
