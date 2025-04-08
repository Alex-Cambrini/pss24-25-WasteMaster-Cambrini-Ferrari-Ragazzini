package it.unibo.wastemaster.core.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "scheduled_collections")
public class CollectionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduledCollectionId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    private ScheduledCollectionStatus status;

    @Enumerated(EnumType.STRING)
    private Waste.WasteType wasteType;

    @OneToMany(mappedBy = "scheduledCollection")
    private List<Collection> collections;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Temporal(TemporalType.DATE)
    private Date nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    public enum ScheduledCollectionStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public CollectionPlan(Customer customer, Frequency frequency, ScheduledCollectionStatus status,
            Waste.WasteType wasteType) {
        this.customer = customer;
        this.frequency = frequency;
        this.status = status;
        this.wasteType = wasteType;
        this.creationDate = new Date();
    }

    public Date creationDate() {
        return creationDate;
    }

    public void creationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getNextCollectionDate() {
        return nextCollectionDate;
    }

    public void setNextCollectionDate(Date nextCollectionDate) {
        this.nextCollectionDate = nextCollectionDate;
    }

    public Waste.WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(Waste.WasteType wasteType) {
        this.wasteType = wasteType;
    }

    public int getScheduledCollectionId() {
        return scheduledCollectionId;
    }

    public void setScheduledCollectionId(int scheduledCollectionId) {
        this.scheduledCollectionId = scheduledCollectionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public ScheduledCollectionStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduledCollectionStatus status) {
        this.status = status;
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
                "ScheduledCollection {ID: %d, Customer: %s, Frequency: %s, Status: %s, StartDate: %s, Collections: %d}",
                scheduledCollectionId, customer != null ? customer.getName() : "N/A", frequency, status, creationDate,
                collections.size());
    }
}