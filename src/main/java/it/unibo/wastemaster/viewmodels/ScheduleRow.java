package it.unibo.wastemaster.viewmodels;

import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import java.time.LocalDate;

/**
 * ViewModel class for displaying schedule information in tables. Supports both one-time
 * and recurring schedules.
 */
public final class ScheduleRow {

    private final int id;
    private final String wasteName;
    private final Schedule.ScheduleCategory scheduleCategory;
    private final RecurringSchedule.Frequency frequency;
    private final LocalDate executionDate;
    private final LocalDate startDate;
    private final Schedule.ScheduleStatus status;
    private final String customer;

    /**
     * Creates a row from a one-time schedule.
     *
     * @param schedule the one-time schedule
     */
    public ScheduleRow(final OneTimeSchedule schedule) {
        this.id = schedule.getScheduleId();
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleCategory = Schedule.ScheduleCategory.ONE_TIME;
        this.frequency = null;
        this.executionDate = schedule.getPickupDate();
        this.startDate = null;
        this.status = schedule.getScheduleStatus();
        this.customer = schedule.getCustomer().getName() + " "
                + schedule.getCustomer().getSurname();
    }

    /**
     * Creates a row from a recurring schedule.
     *
     * @param schedule the recurring schedule
     */
    public ScheduleRow(final RecurringSchedule schedule) {
        this.id = schedule.getScheduleId();
        this.wasteName = schedule.getWaste().getWasteName();
        this.scheduleCategory = Schedule.ScheduleCategory.RECURRING;
        this.frequency = schedule.getFrequency();
        this.executionDate = schedule.getNextCollectionDate();
        this.startDate = schedule.getStartDate();
        this.status = schedule.getScheduleStatus();
        this.customer = schedule.getCustomer().getName() + " "
                + schedule.getCustomer().getSurname();
    }

    /**
     * Gets the schedule ID.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the waste.
     *
     * @return the waste name
     */
    public String getWasteName() {
        return wasteName;
    }

    /**
     * Gets the category of the schedule.
     *
     * @return the schedule category
     */
    public Schedule.ScheduleCategory getScheduleCategory() {
        return scheduleCategory;
    }

    /**
     * Gets the recurrence frequency.
     *
     * @return the frequency, or null if one-time
     */
    public RecurringSchedule.Frequency getFrequency() {
        return frequency;
    }

    /**
     * Gets the next execution date.
     *
     * @return the execution date
     */
    public LocalDate getExecutionDate() {
        return executionDate;
    }

    /**
     * Gets the start date for recurring schedules.
     *
     * @return the start date, or null if one-time
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the status of the schedule.
     *
     * @return the schedule status
     */
    public Schedule.ScheduleStatus getStatus() {
        return status;
    }

    /**
     * Gets the full name of the customer.
     *
     * @return the customer's name and surname
     */
    public String getCustomer() {
        return customer;
    }
}
