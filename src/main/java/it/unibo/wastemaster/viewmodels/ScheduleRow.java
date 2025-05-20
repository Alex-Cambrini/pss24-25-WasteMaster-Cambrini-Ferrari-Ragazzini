package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;

public class ScheduleRow {
    private final int id;
    private final String wasteName;
    private final String scheduleType;
    private final String frequency;
    private final String executionDate;
    private final String startDate;
    private final String status;
    private final String customer;

    public ScheduleRow(OneTimeSchedule schedule) {
        this.id = schedule.getScheduleId();        
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleType = "One Time";
        this.frequency = "N/A";
        this.executionDate = schedule.getPickupDate().toString();
        this.startDate = "N/A";
        this.status = schedule.getScheduleStatus().toString();
        this.customer = schedule.getCustomer().getName() + " " + schedule.getCustomer().getSurname();
    }
    public ScheduleRow(RecurringSchedule schedule) {
        this.id = schedule.getScheduleId();
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleType = "Recurring";
        this.frequency = schedule.getFrequency().toString();
        this.executionDate = schedule.getNextCollectionDate().toString();
        this.startDate = schedule.getStartDate().toString();
        this.status = schedule.getScheduleStatus().toString();
        this.customer = schedule.getCustomer().getName() + " " + schedule.getCustomer().getSurname();
    }

    public int getId() {
        return id;
    }

    public String getWasteName() {
        return wasteName;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomer() {
        return customer;
    }
}
