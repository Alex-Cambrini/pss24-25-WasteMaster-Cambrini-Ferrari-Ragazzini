package it.unibo.wastemaster.core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


/**
 * Represents an invoice entity linked to a completed collection. This class is a JPA
 * entity mapped to the "invoices" table. Each invoice is associated with exactly one
 * completed collection. It stores invoice details including ID, related collection, issue
 * date, payment status, and amount.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int invoiceId;

    @OneToOne(optional = false)
    @JoinColumn(name = "collection_id", nullable = false, unique = true)
    @NotNull(message = "The collection cannot be null")
    private Collection collection;

    @Column(nullable = false)
    @NotNull(message = "The issue date cannot be null")
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "The payment status cannot be null")
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private double amount;

    /**
     * Default constructor required by Hibernate.
     */
    public Invoice() {
        // empty constructor required by Hibernate
    }

    /**
     * Constructs an Invoice for a given collection.
     * 
     * @param collection the collection to invoice; must not be null and must be COMPLETED
     * @throws IllegalArgumentException if collection is null or not completed
     */
    public Invoice(final Collection collection) {
        if (collection == null || collection
                .getCollectionStatus() != Collection.CollectionStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Cannot create invoice for null or non-completed collection.");
        }
        this.collection = collection;
        this.issueDate = LocalDate.now();
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    /**
     * Returns the invoice ID.
     *
     * @return the invoice ID
     */
    public int getInvoiceId() {
        return invoiceId;
    }

    /**
     * Returns the associated collection.
     *
     * @return the collection
     */
    public Collection getCollection() {
        return collection;
    }

    /**
     * Sets the associated collection.
     *
     * @param collection the collection to set
     */
    public void setCollection(final Collection collection) {
        this.collection = collection;
    }

    /**
     * Returns the issue date of the invoice.
     *
     * @return the issue date
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the issue date of the invoice.
     *
     * @param issueDate the issue date to set
     */
    public void setIssueDate(final LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Returns the payment status.
     *
     * @return the payment status
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the payment status.
     *
     * @param paymentStatus the payment status to set
     */
    public void setPaymentStatus(final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * Returns the invoice amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the invoice amount.
     *
     * @param amount the amount to set
     */
    public void setAmount(final double amount) {
        this.amount = amount;
    }

    /**
     * Returns a string representation of the invoice, showing ID, collection ID,
     * customer, waste, amount, issue date, and payment status. Null values are shown as
     * "N/A".
     *
     * Subclasses overriding this method should call {@code super.toString()} to retain
     * details.
     *
     * @return formatted string summarizing the invoice
     */
    @Override
    public String toString() {
        return String.format(
                "Invoice {ID: %d, CollectionID: %s, Customer: %s, Waste: %s, Amount: %"
                        + ".2f, IssueDate: %s, Status: %s}",
                invoiceId, collection != null ? collection.getCollectionId() : "N/A",
                collection != null && collection.getCustomer() != null
                        ? collection.getCustomer().getName()
                        : "N/A",
                collection != null && collection.getWaste() != null
                        ? collection.getWaste().getWasteName()
                        : "N/A",
                amount, issueDate != null ? issueDate.toString() : "N/A",
                paymentStatus != null ? paymentStatus.name() : "N/A");
    }

    /**
     * Enum representing the payment status of an invoice.
     */
    public enum PaymentStatus {
        /** Payment completed. */
        PAID,
        /** Payment not completed. */
        UNPAID,
        /** Payment is pending. */
        PENDING
    }


}
