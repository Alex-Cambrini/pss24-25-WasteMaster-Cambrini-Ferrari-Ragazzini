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



}