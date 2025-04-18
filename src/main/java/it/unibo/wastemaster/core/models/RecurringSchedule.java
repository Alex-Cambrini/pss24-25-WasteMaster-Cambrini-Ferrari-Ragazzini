package it.unibo.wastemaster.core.models;

import java.util.Date;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    public RecurringSchedule() {
    }

    public RecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Date startDate,
            Frequency frequency) {
        super(customer, wasteType, status);
        ValidateUtils.validateNotNull(startDate, "Start date must not be null");
        ValidateUtils.validateNotNull(frequency, "Frequency must not be null");
        this.frequency = frequency;
        this.startDate = startDate;
        this.nextCollectionDate = null;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        ValidateUtils.validateNotNull(startDate, "Start date must not be null");
        this.startDate = startDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        ValidateUtils.validateNotNull(frequency, "Frequency must not be null");
        this.frequency = frequency;
    }

    public Date getNextCollectionDate() {
        return nextCollectionDate;
    }

    public void setNextCollectionDate(Date nextCollectionDate) {
        this.nextCollectionDate = nextCollectionDate;
    }
}