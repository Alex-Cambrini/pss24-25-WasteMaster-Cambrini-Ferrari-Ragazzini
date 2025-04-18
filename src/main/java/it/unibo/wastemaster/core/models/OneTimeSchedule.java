package it.unibo.wastemaster.core.models;

import java.util.Date;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OneTimeSchedule extends Schedule {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date pickupDate;
    
    // No-args constructor required by JPA
    public OneTimeSchedule() {}

    public OneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Date pickupDate) {
        super(customer, wasteType, status);
        ValidateUtils.validateNotNull(pickupDate, "pickupDate must not be null");
        this.pickupDate = pickupDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        ValidateUtils.validateNotNull(pickupDate, "pickupDate must not be null");
        this.pickupDate = pickupDate;
    }
}
