package it.unibo.wastemaster.core.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Represents a one-time schedule for waste collection. Extends the base Schedule class
 * and adds a specific pickup date.
 */
@Entity
@DiscriminatorValue("ONE_TIME")
public class OneTimeSchedule extends Schedule {

    /**
     * The pickup date for this one-time schedule. Must be today or in the future.
     */
    @NotNull(message = "Pickup date must not be null")
    @FutureOrPresent(message = "Pickup date must be today or in the future")
    private LocalDate pickupDate;

    /**
     * No-args constructor required by JPA.
     */
    public OneTimeSchedule() { }

    /**
     * Constructs a OneTimeSchedule with the given customer, waste, and pickup date.
     *
     * @param customer the customer associated with this schedule
     * @param waste the waste type to be collected
     * @param pickupDate the scheduled pickup date (today or future)
     */
    public OneTimeSchedule(final Customer customer, final Waste waste,
            final LocalDate pickupDate) {
        super(customer, waste);
        this.pickupDate = pickupDate;
        this.setScheduleCategory(ScheduleCategory.ONE_TIME);
    }

    /**
     * Returns the pickup date.
     *
     * @return the pickup date
     */
    public LocalDate getPickupDate() {
        return pickupDate;
    }

    /**
     * Sets the pickup date.
     *
     * @param pickupDate the new pickup date
     */
    public void setPickupDate(final LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    /**
     * Returns the collection date, same as the pickup date for one-time schedules.
     *
     * @return the collection date
     */
    @Override
    public LocalDate getCollectionDate() {
        return this.getPickupDate();
    }

    /**
     * Returns a string representation of this schedule including pickup date.
     *
     * @return string with schedule details
     */
    @Override
    public String toString() {
        return super.toString() + String.format(", PickupDate: %s",
                pickupDate != null ? pickupDate.toString() : "N/A");
    }
}
