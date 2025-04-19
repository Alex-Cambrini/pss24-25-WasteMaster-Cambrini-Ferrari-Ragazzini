package it.unibo.wastemaster.core.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    
    @NotNull 
    private LocalDate startDate;

    private LocalDate nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    public RecurringSchedule() {
    }

    public RecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, LocalDate startDate,
            Frequency frequency) {
        super(customer, wasteType, status);
        this.frequency = frequency;
        this.startDate = startDate;
        this.nextCollectionDate = null;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getNextCollectionDate() {
        return nextCollectionDate;
    }

    public void setNextCollectionDate(LocalDate nextCollectionDate) {
        this.nextCollectionDate = nextCollectionDate;
    }
}