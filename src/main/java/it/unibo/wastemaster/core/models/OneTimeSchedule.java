package it.unibo.wastemaster.core.models;

import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("ONE_TIME")
public class OneTimeSchedule extends Schedule {

    @NotNull(message = "Pickup date must not be null")
    @FutureOrPresent(message = "Pickup date must be today or in the future")
    private LocalDate pickupDate;

    // No-args constructor required by JPA
    public OneTimeSchedule() {
    }

    public OneTimeSchedule(Customer customer, Waste waste, LocalDate pickupDate) {
        super(customer, waste);
        this.pickupDate = pickupDate;
        this.setScheduleCategory(ScheduleCategory.ONE_TIME);
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    @Override
    public LocalDate getCollectionDate() {
        return this.getPickupDate();
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", PickupDate: %s",
                pickupDate != null ? pickupDate.toString() : "N/A");
    }
}
