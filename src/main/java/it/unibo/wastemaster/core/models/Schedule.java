package it.unibo.wastemaster.core.models;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;

import java.util.Date;
import java.util.List;

import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;

@Entity
public abstract class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Waste.WasteType wasteType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;

    @OneToMany(mappedBy = "schedule")
    private List<Collection> collections;

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
        ValidateUtils.validateNotNull(customer, "Customer cannot be null");
        ValidateUtils.validateNotNull(wasteType, "WasteType cannot be null");
        ValidateUtils.validateNotNull(status, "Status cannot be null");
        
        this.customer = customer;
        this.wasteType = wasteType;
        this.status = status;
        this.creationDate = DateUtils.getCurrentDate();
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        ValidateUtils.validateNotNull(customer, "Customer cannot be null");
        this.customer = customer;
    }

    public Waste.WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(Waste.WasteType wasteType) {
        ValidateUtils.validateNotNull(wasteType, "WasteType cannot be null");
        this.wasteType = wasteType;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        ValidateUtils.validateNotNull(status, "Status cannot be null");
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        ValidateUtils.validateNotNull(creationDate, "CreationDate cannot be null");
        this.creationDate = creationDate;
    }

    public List<Collection> getCollections() { 
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    @Override
    public String toString() {
        return String.format(
                "Schedule {Customer: %s, WasteType: %s, Status: %s, CreationDate: %s, CollectionIDs: %s}",
                customer != null ? customer.getName() : "N/A",
                wasteType != null ? wasteType : "N/A",
                status != null ? status : "N/A",
                creationDate != null ? creationDate.toString() : "N/A",
                collections != null ? collections.stream()
                        .map(c -> String.valueOf(c.getCollectionId()))
                        .reduce((id1, id2) -> id1 + ", " + id2).orElse("N/A") : "N/A");
    }
}
