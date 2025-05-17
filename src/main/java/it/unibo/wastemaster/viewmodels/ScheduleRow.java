package it.unibo.wastemaster.viewmodels;

public class ScheduleRow {
    private final String wasteType;
    private final String scheduleType;
    private final String frequency;
    private final String pickupDate;
    private final String nextCollectionDate;
    private final String startDate;
    private final String status;
    private final String customerName;
    private final String customerSurname;

    public ScheduleRow(String wasteType, String scheduleType, String frequency, String pickupDate,
            String nextCollectionDate, String startDate, String status,
            String customerName, String customerSurname) {
        this.wasteType = wasteType;
        this.scheduleType = scheduleType;
        this.frequency = frequency;
        this.pickupDate = pickupDate;
        this.nextCollectionDate = nextCollectionDate;
        this.startDate = startDate;
        this.status = status;
        this.customerName = customerName;
        this.customerSurname = customerSurname;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getNextCollectionDate() {
        return nextCollectionDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }
}
