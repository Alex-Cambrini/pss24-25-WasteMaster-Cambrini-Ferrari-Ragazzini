package it.unibo.wastemaster.viewmodels;

import java.time.LocalDate;

import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleCategory;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;

public class ScheduleRow {
    private final int id;
    private final String wasteName;
    private final ScheduleCategory scheduleCategory;
    private final Frequency frequency;
    private final LocalDate executionDate;
    private final LocalDate startDate;
    private final ScheduleStatus status;
    private final String customer;

    public ScheduleRow(OneTimeSchedule schedule) {
        this.id = schedule.getScheduleId();
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleCategory = ScheduleCategory.ONE_TIME;
        this.frequency = null;
        this.executionDate = schedule.getPickupDate();
        this.startDate = null;
        this.status = schedule.getScheduleStatus();
        this.customer = schedule.getCustomer().getName() + " " + schedule.getCustomer().getSurname();
    }

    public ScheduleRow(RecurringSchedule schedule) {
        this.id = schedule.getScheduleId();
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleCategory = ScheduleCategory.RECURRING;
        this.frequency = schedule.getFrequency();
        this.executionDate = schedule.getNextCollectionDate();
        this.startDate = schedule.getStartDate();
        this.status = schedule.getScheduleStatus();
        this.customer = schedule.getCustomer().getName() + " " + schedule.getCustomer().getSurname();
    }

    public int getId() {
        return id;
    }

    public String getWasteName() {
        return wasteName;
    }

    public ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public String getCustomer() {
        return customer;
    }
}
