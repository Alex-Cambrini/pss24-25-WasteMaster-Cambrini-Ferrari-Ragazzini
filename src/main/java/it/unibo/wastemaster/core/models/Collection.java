package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int collectionId;

    @ManyToOne
    private Customer customer;

    private Date date;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType waste;

    @Enumerated(EnumType.STRING)
    private CollectionStatus collectionStatus;

    private int cancelLimitDays;
    private int scheduleId;
    private boolean isExtra;

    public enum CollectionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Collection(Customer customer, Date date, Waste.WasteType waste, CollectionStatus collectionStatus, int cancelLimitDays, int scheduleId, boolean isExtra) {
        this.customer = customer;
        this.date = date;
        this.waste = waste;
        this.collectionStatus = collectionStatus;
        this.cancelLimitDays = cancelLimitDays;
        this.scheduleId = scheduleId;
        this.isExtra = isExtra;
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

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean isExtra) {
        this.isExtra = isExtra;
    }

    public String getInfo() {
        return String.format("Collection ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %d, Extra: %b",
                collectionId, customer.getName(), date.toString(), waste, collectionStatus, cancelLimitDays, scheduleId, isExtra);
    }

}
