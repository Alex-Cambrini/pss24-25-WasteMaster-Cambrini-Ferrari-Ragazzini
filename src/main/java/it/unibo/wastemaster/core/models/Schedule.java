package it.unibo.wastemaster.core.models;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.sql.Date;

@MappedSuperclass
public abstract class Schedule {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType wasteType;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @OneToOne
    @JoinColumn(name = "collection_id", unique = true)
    private Collection collection;

    public enum ScheduleStatus {
        SCHEDULED,
        ACTIVE,
        CANCELLED,
        STOPPED
    }

    // No-args constructor required by JPA
    public Schedule() {
    }

    public Schedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status) {
        this.customer = customer;
        this.wasteType = wasteType;
        this.status = status;
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Waste.WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(Waste.WasteType wasteType) {
        this.wasteType = wasteType;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return String.format(
                "Schedule {Customer: %s, WasteType: %s, Status: %s, CreationDate: %s, CollectionID: %s}",
                customer != null ? customer.getName() : "N/A",
                wasteType != null ? wasteType : "N/A",
                status != null ? status : "N/A",
                creationDate != null ? creationDate.toString() : "N/A",
                collection != null ? collection.getCollectionId() : "N/A");
    }

}
