package it.unibo.wastemaster.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;


@Entity
@Table(name = "collections")
public class Collection {

    public static final int CANCEL_LIMIT_DAYS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int collectionId;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "The customer cannot be null")
    private Customer customer;
    
    @FutureOrPresent(message = "The date must be today or in the future")
    @NotNull(message = "The date cannot be null")
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "The waste type cannot be null")
    @Column(nullable = false)
    private Waste.WasteType waste;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "The collection status cannot be null")
    @Column(nullable = false)
    private CollectionStatus collectionStatus;
    
    @Min(value = 0, message = "Cancellation days must be >= 0")
    @Column(nullable = false)
    private int cancelLimitDays;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @Column(nullable = false)
    private Schedule schedule;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCategory scheduleCategory;
    
    @Column(nullable = false)
    private boolean isExtra;

    public enum ScheduleCategory {
        ONE_TIME,
        RECURRING
    }

    public enum CollectionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Collection() {
    }

    public Collection(Customer customer, LocalDate date, Waste.WasteType waste, CollectionStatus collectionStatus,
            Schedule schedule, ScheduleCategory scheduleCategory) {
        this.customer = customer;
        this.date = date;
        this.waste = waste;
        this.collectionStatus = collectionStatus;
        this.cancelLimitDays = CANCEL_LIMIT_DAYS;
        this.schedule = schedule;
        this.scheduleCategory = scheduleCategory;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Waste.WasteType getWaste() {
        return waste;
    }

    public void setWaste(Waste.WasteType waste) {
        this.waste = waste;
    }

    public CollectionStatus getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(CollectionStatus collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    public void setCancelLimitDays(int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    public void setScheduleCategory(ScheduleCategory scheduleCategory) {
        this.scheduleCategory = scheduleCategory;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean isExtra) {
        this.isExtra = isExtra;
    }

    @Override
    public String toString() {
        return String.format(
                "Collection {ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %s, Schedule Category: %s}",
                collectionId,
                customer != null ? customer.getName() : "N/A",
                date != null ? date.toString() : "N/A",
                waste,
                collectionStatus,
                cancelLimitDays,
                schedule,
                scheduleCategory);
    }
}