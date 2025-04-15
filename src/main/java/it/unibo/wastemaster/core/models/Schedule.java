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

import java.sql.Date;
import java.util.List;

@Entity
public abstract class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType wasteType;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Temporal(TemporalType.DATE)
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
        this.customer = customer;
        this.wasteType = wasteType;
        this.status = status;
        this.creationDate = new Date(System.currentTimeMillis());
    }

    public int getId() {
        return id;
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
