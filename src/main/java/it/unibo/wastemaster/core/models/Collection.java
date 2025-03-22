package it.unibo.wastemaster.core.models;

import java.util.Date;
public class Collection {
    private int collectionId;
    private Customer customer;
    private Date date;
    private Waste.WasteType waste;
    private CollectionStatus status;
    private int cancelLimitDays;
    private int scheduleId;
    private boolean isExtra;

    public enum CollectionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public Collection(int collectionId, Customer customer, Date date, Waste.WasteType waste, CollectionStatus status, int cancelLimitDays, int scheduleId, boolean isExtra) {
        this.collectionId = collectionId;
        this.customer = customer;
        this.date = date;
        this.waste = waste;
        this.status = status;
        this.cancelLimitDays = cancelLimitDays;
        this.scheduleId = scheduleId;
        this.isExtra = isExtra;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Waste.WasteType getWaste() {
        return waste;
    }

    public void setWaste(Waste.WasteType waste) {
        this.waste = waste;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public int getCancelLimitDays() {
        return cancelLimitDays;
    }

    public void setCancelLimitDays(int cancelLimitDays) {
        this.cancelLimitDays = cancelLimitDays;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean isExtra) {
        this.isExtra = isExtra;
    }

    public String getInfo() {
        return String.format("Collection ID: %d, Customer: %s, Date: %s, Waste: %s, Status: %s, Cancel Limit Days: %d, Schedule ID: %d, Extra: %b",
                collectionId, customer.getName(), date.toString(), waste, status, cancelLimitDays, scheduleId, isExtra);
    }


    //TEST

    // public static void main(String[] args) {
    //     Customer customer = new Customer(1, "Mario", "Rossi", new Location(0, "Via Roma", "10", "Milano", "Italy"), "mario@example.com", "1234567890", 12345);
        
    //     Waste waste = new Waste(1, Waste.WasteType.PAPER, true, false);

    //     Collection collection = new Collection(
    //             1, 
    //             customer, 
    //             new Date(), 
    //             waste.getType(), 
    //             Collection.Status.PENDING, 
    //             3, 
    //             101, 
    //             false
    //     );

    //     if (collection.getCollectionId() == 1 && 
    //         collection.getCustomer() == customer && 
    //         collection.getDate() != null && 
    //         collection.getWaste() == Waste.WasteType.PAPER && 
    //         collection.getStatus() == Collection.Status.PENDING && 
    //         collection.getCancelLimitDays() == 3 && 
    //         collection.getScheduleId() == 101 && 
    //         !collection.isExtra()) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }


    //     collection.setStatus(Collection.Status.IN_PROGRESS);
    //     collection.setExtra(true);
    //     collection.setCancelLimitDays(5);
        
    //     if (collection.getStatus() == Collection.Status.IN_PROGRESS && 
    //         collection.isExtra() && 
    //         collection.getCancelLimitDays() == 5) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error");
    //     }

 
    //     String info = collection.getInfo();
    //     if (info.contains("Collection ID: 1") && 
    //         info.contains("Customer: Mario") && 
    //         info.contains("Waste: PAPER") && 
    //         info.contains("Status: IN_PROGRESS") &&
    //         info.contains("Cancel Limit Days: 5")) {
    //         System.out.println("ok");
    //     } else {
    //         System.out.println("error"); 
    //     }
    // }



}
