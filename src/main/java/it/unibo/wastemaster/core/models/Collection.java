package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.Date;



@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int collectionId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Date date;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType waste;

    @Enumerated(EnumType.STRING)
    private CollectionStatus collectionStatus;

    private int cancelLimitDays;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    
    private ScheduleCategory scheduleCategory;

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

    @Column(nullable = false)
    private boolean isExtra;

    // No-args constructor required by JPA
    public Collection() {}

    public Collection(Customer customer, Date date, Waste.WasteType waste, CollectionStatus collectionStatus, int cancelLimitDays, Schedule schedule, ScheduleCategory scheduleCategory) {
        this.customer = customer;
        this.date = date;
        this.waste = waste;
        this.collectionStatus = collectionStatus;
        this.cancelLimitDays = cancelLimitDays;
        this.scheduleCategory = scheduleCategory;
        this.schedule = schedule;
    }

    // Getters e Setters
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Schedule getScheduleId() {
        return schedule;
    }

    public void setScheduleId(Schedule schedule) {
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
            scheduleCategory
        );
    }
}
