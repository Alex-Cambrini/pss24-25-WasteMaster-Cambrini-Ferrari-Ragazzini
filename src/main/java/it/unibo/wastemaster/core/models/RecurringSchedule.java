package it.unibo.wastemaster.core.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("RECURRING")
public class RecurringSchedule extends Schedule {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Frequency cannot be null")
    private Frequency frequency;

    @NotNull(message = "Start Date cannot be null")
    private LocalDate startDate;

    private LocalDate nextCollectionDate;

    public enum Frequency {
        WEEKLY,
        MONTHLY
    }

    public RecurringSchedule() {
    }

    public RecurringSchedule(Customer customer, Waste waste, LocalDate startDate,
            Frequency frequency) {
        super(customer, waste);
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

    @Override
    public String toString() {
        return super.toString() + String.format(", StartDate: %s, Frequency: %s, NextCollection: %s",
                startDate != null ? startDate.toString() : "N/A",
                frequency != null ? frequency.name() : "N/A",
                nextCollectionDate != null ? nextCollectionDate.toString() : "N/A");
    }
}