package it.unibo.wastemaster.core.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Represents a recurring schedule for waste collection.
 */
@Entity
@DiscriminatorValue("RECURRING")
public class RecurringSchedule extends Schedule {

    /**
     * Frequency options for recurring waste collection.
     */
    public enum Frequency {
        /**
         * Collection occurs every week.
         */
        WEEKLY,

        /**
         * Collection occurs every month.
         */
        MONTHLY
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Frequency cannot be null")
    private Frequency frequency;

    @NotNull(message = "Start Date cannot be null")
    private LocalDate startDate;

    private LocalDate nextCollectionDate;

    /**
     * Default constructor for JPA.
     */
    public RecurringSchedule() { }

    /**
     * Constructs a RecurringSchedule with specified customer, waste, start date and
     * frequency.
     *
     * @param customer the customer linked to this schedule
     * @param waste the type of waste to be collected
     * @param startDate the date when the recurring schedule starts
     * @param frequency how often the collection recurs
     */
    public RecurringSchedule(final Customer customer, final Waste waste,
            final LocalDate startDate, final Frequency frequency) {
        super(customer, waste);
        this.startDate = startDate;
        this.frequency = frequency;
        this.setScheduleCategory(ScheduleCategory.RECURRING);
        this.nextCollectionDate = null;
    }

    /**
     * Returns the start date of the recurring schedule.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the recurring schedule.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the frequency of the recurring collection.
     *
     * @return the frequency (WEEKLY or MONTHLY)
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of the recurring collection.
     *
     * @param frequency the frequency to set
     */
    public void setFrequency(final Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the next scheduled collection date.
     *
     * @return the next collection date, or null if not set
     */
    public LocalDate getNextCollectionDate() {
        return nextCollectionDate;
    }

    /**
     * Sets the next scheduled collection date.
     *
     * @param nextCollectionDate the next collection date to set
     */
    public void setNextCollectionDate(final LocalDate nextCollectionDate) {
        this.nextCollectionDate = nextCollectionDate;
    }

    /**
     * Returns the date of the next collection.
     *
     * @return the next collection date
     */
    @Override
    public LocalDate getCollectionDate() {
        return this.getNextCollectionDate();
    }

    /**
     * Returns a string representation of the recurring schedule.
     *
     * @return string describing the recurring schedule
     */
    @Override
    public String toString() {
        return super.toString() + String.format(
                ", StartDate: %s, Frequency: %s, NextCollection: %s",
                startDate != null ? startDate.toString() : "N/A",
                frequency != null ? frequency.name() : "N/A",
                nextCollectionDate != null ? nextCollectionDate.toString() : "N/A");
    }
}
