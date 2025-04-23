package it.unibo.wastemaster.core.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("RECURRING")
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Frequency cannot be null")
    private Frequency frequency;
    

    @FutureOrPresent(message = "Start Date must be today or in the future")
    @NotNull(message = "Start Date cannot be null")
    private LocalDate startDate;

    private LocalDate nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    public RecurringSchedule() {
    }

    public RecurringSchedule(Customer customer, Waste.WasteType wasteType, LocalDate startDate,
            Frequency frequency) {
        super(customer, wasteType);
        this.startDate = startDate;
        this.frequency = frequency;
        this.setScheduleCategory(ScheduleCategory.RECURRING);
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

    @Override
    public LocalDate getCollectionDate() {
        return this.getNextCollectionDate();
    }
}