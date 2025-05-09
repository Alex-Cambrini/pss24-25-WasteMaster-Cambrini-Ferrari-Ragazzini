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
    @JoinColumn(name = "schedule_id", nullable = false)
    @NotNull(message = "Schedule cannot be null")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "trip_id") 
    private Trip trip;

    public enum CollectionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Collection() {
    }

    public Collection(Schedule schedule) {
    this.schedule = schedule;
    if (schedule != null) {
        this.date       = schedule.getCollectionDate();
        this.waste      = schedule.getWasteType();
        this.customer   = schedule.getCustomer();
    }
    this.collectionStatus = CollectionStatus.PENDING;
    this.cancelLimitDays  = CANCEL_LIMIT_DAYS;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public LocalDate getCollectionDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Waste.WasteType getWaste() {
        return waste;
    }

    public CollectionStatus getCollectionStatus() {
        return collectionStatus;
    }

    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setCollectionDate(LocalDate date) {
        this.date = date;
    }

    public void setWaste(Waste.WasteType waste) {
        this.waste = waste;
    }    

    public void setCollectionStatus(CollectionStatus collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public void setCancelLimitDays(int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    @Override
    public String toString() {
        return String.format(
                "Collection {ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %s, Schedule Category: %s}",
                collectionId,
                customer.getName(),
                date,
                waste,
                collectionStatus,
                cancelLimitDays,
                schedule.getScheduleId(),
                schedule.getScheduleCategory());
    }
}