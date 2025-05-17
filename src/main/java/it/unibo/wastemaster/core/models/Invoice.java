package it.unibo.wastemaster.core.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;



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
    @NotNull(message = "The amount cannot be null")
    @Positive(message = "The amount must be positive")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull(message = "The issue date cannot be null")
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "The payment status cannot be null")
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PAID,
        UNPAID,
        PENDING
    }

    public Invoice() {}

    public Invoice(Collection collection, BigDecimal amount, LocalDate issueDate, PaymentStatus paymentStatus) {
        this.collection = collection;
        this.amount = amount;
        this.issueDate = issueDate;
        this.paymentStatus = paymentStatus;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
    return String.format(
        "Invoice {ID: %d, CollectionID: %s, Customer: %s, Waste: %s, Amount: %s, IssueDate: %s, Status: %s}",
        invoiceId,
        collection != null ? collection.getCollectionId() : "N/A",
        collection != null && collection.getCustomer() != null ? collection.getCustomer().getName() : "N/A",
        collection != null && collection.getWaste() != null ? collection.getWaste().getWasteName() : "N/A",
        amount != null ? amount.toString() : "N/A",
        issueDate != null ? issueDate.toString() : "N/A",
        paymentStatus != null ? paymentStatus.name() : "N/A"
    );
    }
}