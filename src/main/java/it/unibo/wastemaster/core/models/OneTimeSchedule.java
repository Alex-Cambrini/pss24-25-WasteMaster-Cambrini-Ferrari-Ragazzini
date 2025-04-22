package it.unibo.wastemaster.core.models;

import java.time.LocalDate;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OneTimeSchedule extends Schedule {

    @NotNull (message = "Pickup date must not be null")
    @FutureOrPresent (message = "Pickup date must be today or in the future")
    @Column(nullable = false)
    private LocalDate pickupDate;    
    
    // No-args constructor required by JPA
    public OneTimeSchedule() {}

    public OneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, LocalDate pickupDate) {
        super(customer, wasteType, status);
        ValidateUtils.validateNotNull(pickupDate, "pickupDate must not be null");
        this.pickupDate = pickupDate;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        ValidateUtils.validateNotNull(pickupDate, "pickupDate must not be null");
        this.pickupDate = pickupDate;
    }
}
