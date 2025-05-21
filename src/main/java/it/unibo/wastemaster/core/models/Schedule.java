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

@jakarta.persistence.Inheritance(strategy = jakarta.persistence.InheritanceType.SINGLE_TABLE)
@jakarta.persistence.DiscriminatorColumn(name = "schedule_type", discriminatorType = jakarta.persistence.DiscriminatorType.STRING)

public abstract class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer cannot be null")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "waste_id", nullable = false)
    @NotNull(message = "WasteType cannot be null")
    private Waste waste;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private ScheduleStatus status;

    @Column(nullable = false)
    @NotNull(message = "CreationDate cannot be null")
    private LocalDate creationDate;

    @Valid
    @OneToMany(mappedBy = "schedule")
    private List<Collection> collections;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Schedule category cannot be null")
    @Column(nullable = false)
    private ScheduleCategory scheduleCategory;

    public enum ScheduleCategory {
        ONE_TIME,
        RECURRING
    }

    public enum ScheduleStatus {
        ACTIVE,
        CANCELLED,
        PAUSED,
        COMPLETED
    }

    // No-args constructor required by JPA
    public Schedule() {
    }

    public Schedule(Customer customer, Waste waste) {
        this.customer = customer;
        this.waste = waste;
        this.status = ScheduleStatus.ACTIVE;
        this.creationDate = new DateUtils().getCurrentDate();
    }

    public abstract LocalDate getCollectionDate();

    public int getScheduleId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Waste getWaste() {
        return waste;
    }

    public void setWaste(Waste waste) {
        this.waste = waste;
    }

    public ScheduleStatus getScheduleStatus() {
        return status;
    }

    public void setScheduleStatus(ScheduleStatus status) {
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

    public ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    public void setScheduleCategory(ScheduleCategory scheduleCategory) {
        this.scheduleCategory = scheduleCategory;
    }

    @Override
    public String toString() {
        return String.format(
                "%s Schedule {Customer: %s, WasteType: %s, Status: %s, CreationDate: %s, CollectionIDs: %s}",
                scheduleCategory != null ? scheduleCategory.name() : "Unknown",
                customer != null ? customer.getName() : "N/A",
                waste != null ? waste.getWasteName(): "N/A",
                status != null ? status.name() : "N/A",
                creationDate != null ? creationDate : "N/A",
                collections != null && !collections.isEmpty()
                        ? collections.stream().map(c -> String.valueOf(c.getCollectionId()))
                                .reduce((a, b) -> a + ", " + b).orElse("N/A")
                        : "None");
    }
}