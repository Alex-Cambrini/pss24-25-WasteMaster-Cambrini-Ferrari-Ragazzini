package it.unibo.wastemaster.core.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "one_time_schedules")
public class OneTimeSchedule extends Schedule {

    @Temporal(TemporalType.TIMESTAMP)
    private Date pickupDate;
    
    // No-args constructor required by JPA
    public OneTimeSchedule() {}

    public OneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Date pickupDate) {
        super(customer, wasteType, status);  
        this.pickupDate = pickupDate;       
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    @Override
    public String toString() {
        return String.format(
                "OneTimeSchedule {Customer: %s, WasteType: %s, Status: %s, PickupDate: %s, CreationDate: %s, CollectionId: %s}",
                getCustomer() != null ? getCustomer().getName() : "N/A",
                getWasteType(),
                getStatus(),
                pickupDate != null ? pickupDate.toString() : "N/A",
                getCreationDate(),
                getCollection() != null ? getCollection().getCollectionId() : "N/A"
        );
    }
}
