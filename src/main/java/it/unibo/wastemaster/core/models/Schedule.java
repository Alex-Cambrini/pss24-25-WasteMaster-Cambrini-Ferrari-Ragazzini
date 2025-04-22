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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.core.utils.DateUtils;

@Entity
public abstract class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "WasteType cannot be null")
    private Waste.WasteType wasteType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private ScheduleStatus status;

    @NotNull
    @Column(nullable = false)
    @NotNull(message = "CreationDate cannot be null")
    private LocalDate creationDate;

    @Valid
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
        this.creationDate = DateUtils.getCurrentDate();
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
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