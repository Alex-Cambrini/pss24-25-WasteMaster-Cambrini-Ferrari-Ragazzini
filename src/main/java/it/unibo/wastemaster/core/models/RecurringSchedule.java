package it.unibo.wastemaster.core.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    // No-args constructor required by JPA
    public RecurringSchedule() {
    }

    public RecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Date startDate,
            Frequency frequency) {
        super(customer, wasteType, status);
        this.frequency = frequency;
        this.startDate = startDate;
        this.nextCollectionDate = null;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
}
