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

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @OneToMany
    @JoinColumn(name = "invoice_id")
    private List<Collection> collections = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "The customer cannot be null")
    private Customer customer;

    @Column(nullable = false)
    @NotNull(message = "The issue date cannot be null")
    private LocalDateTime issueDate;

    @Column
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "The payment status cannot be null")
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    @NotNull(message = "The last modified date cannot be null")
    private LocalDateTime lastModified;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private double totalRecurring;

    @Column(nullable = false)
    private double totalOnetime;

    @Column(nullable = false)
    private int recurringCount;

    @Column(nullable = false)
    private int onetimeCount;

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Invoice() {
        // required by Hibernate
    }

    public Invoice(Customer customer, List<Collection> collections, 
                double totalRecurring, double totalOnetime, 
                int recurringCount, int onetimeCount, LocalDateTime issueDate) {
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

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(final LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(final PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public double getTotalRecurring() {
        return totalRecurring;
    }

    public void setTotalRecurring(final double totalRecurring) {
        this.totalRecurring = totalRecurring;
    }

    public double getTotalOnetime() {
        return totalOnetime;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(final LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setTotalOnetime(final double totalOnetime) {
        this.totalOnetime = totalOnetime;
    }

    public int getRecurringCount() {
        return recurringCount;
    }

    public void setRecurringCount(final int recurringCount) {
        this.recurringCount = recurringCount;
    }

    public int getOnetimeCount() {
        return onetimeCount;
    }

    public void setOnetimeCount(final int onetimeCount) {
        this.onetimeCount = onetimeCount;
    }

     public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(final boolean deleted) {
        this.isDeleted = deleted;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }
    public enum PaymentStatus {
        PAID,
        UNPAID
    }


    @Override
    public String toString() {
        return "Invoice{id=" + invoiceId +
                ", customer=" + (customer != null ? customer.getName() : "null") +
                ", amount=" + String.format("%.2f", amount) +
                ", status=" + (paymentStatus != null ? paymentStatus.name() : "null") +
                ", date=" + (issueDate != null ? issueDate.toString() : "null") +
                ", collections=" + (collections != null ? collections.size() : 0) +
                "}";
    }
}
