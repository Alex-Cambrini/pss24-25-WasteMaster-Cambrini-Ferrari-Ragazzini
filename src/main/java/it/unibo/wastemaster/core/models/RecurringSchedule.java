package it.unibo.wastemaster.core.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "recurring_schedules")
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Temporal(TemporalType.DATE)
    private Date nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    // No-args constructor required by JPA
    public RecurringSchedule() {}

    public RecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Frequency frequency) {
        super(customer, wasteType, status);
        this.frequency = frequency;
        this.nextCollectionDate = null;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Date getNextCollectionDate() {
        return nextCollectionDate;
    }

    public void setNextCollectionDate(Date nextCollectionDate) {
        this.nextCollectionDate = nextCollectionDate;
    }

    @Override
    public String toString() {
        return String.format(
                "RecurringSchedule {Customer: %s, WasteType: %s, Status: %s, Frequency: %s, NextCollectionDate: %s, CreationDate: %s, CollectionId: %s}",
                getCustomer() != null ? getCustomer().getName() : "N/A",
                getWasteType(),
                getStatus(),
                frequency,
                nextCollectionDate != null ? nextCollectionDate.toString() : "N/A",
                getCreationDate(),
                getCollection() != null ? getCollection().getCollectionId() : "N/A"
        );
    }
}
