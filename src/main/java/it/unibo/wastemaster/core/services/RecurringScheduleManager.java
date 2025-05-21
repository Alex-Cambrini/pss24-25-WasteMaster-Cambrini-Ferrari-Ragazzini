package it.unibo.wastemaster.core.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.core.models.Collection;

public class RecurringScheduleManager {

    private RecurringScheduleDAO recurringScheduleDAO;
    private WasteScheduleManager wasteScheduleManager;
    private CollectionManager collectionManager;
    private DateUtils dateUtils = new DateUtils();

    public RecurringScheduleManager(RecurringScheduleDAO recurringScheduleDAO,
            WasteScheduleManager wasteScheduleManager) {
        this.wasteScheduleManager = wasteScheduleManager;
        this.recurringScheduleDAO = recurringScheduleDAO;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setDateUtils(DateUtils dateUtils) {
        this.dateUtils = dateUtils;
    }

    public RecurringSchedule createRecurringSchedule(Customer customer, Waste waste, LocalDate startDate,
            Frequency frequency) {
        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, frequency);
        LocalDate nextCollectionDate = calculateNextDate(schedule);
        schedule.setNextCollectionDate(nextCollectionDate);
        recurringScheduleDAO.insert(schedule);
        collectionManager.generateCollection(schedule);
        return schedule;
    }

    protected LocalDate calculateNextDate(RecurringSchedule schedule) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule must not be null");
        ValidateUtils.requireArgNotNull(schedule.getScheduleId(), "Schedule ID must not be null");
        LocalDate today = dateUtils.getCurrentDate();

        if (schedule.getNextCollectionDate() == null) {
            return calculateFirstDate(schedule);
        } else {
            return calculateRecurringDate(schedule, today);
        }
    }

    private LocalDate calculateFirstDate(RecurringSchedule schedule) {
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getStartDate().plusDays(2);
        return alignToScheduledDay(date, scheduleData.getDayOfWeek());
    }

    private LocalDate calculateRecurringDate(RecurringSchedule schedule, LocalDate today) {
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getNextCollectionDate();

        if (schedule.getFrequency() == RecurringSchedule.Frequency.WEEKLY) {
            date = date.plusWeeks(1);
        } else {
            date = date.plusMonths(1);
            date = alignToScheduledDay(date, scheduleData.getDayOfWeek());
        }

        while (date.isBefore(today)) {
            date = date.plusDays(1);
            date = alignToScheduledDay(date, scheduleData.getDayOfWeek());
        }

        return date;
    }

    private LocalDate alignToScheduledDay(LocalDate date, DayOfWeek scheduledDay) {
        while (date.getDayOfWeek() != scheduledDay) {
            date = date.plusDays(1);
        }
        return date;
    }

    public List<RecurringSchedule> getRecurringSchedulesWithoutCollections() {
        return recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();
    }

    public void updateNextDates() {
        List<RecurringSchedule> schedules = recurringScheduleDAO.findActiveSchedulesWithNextDateBeforeToday();
        for (RecurringSchedule schedule : schedules) {
            LocalDate nextDate = calculateNextDate(schedule);
            schedule.setNextCollectionDate(nextDate);
            recurringScheduleDAO.update(schedule);
        }
        collectionManager.generateRecurringCollections();
    }

    public List<RecurringSchedule> getSchedulesByCustomer(Customer customer) {
        return recurringScheduleDAO.findSchedulesByCustomer(customer);
    }

    public boolean updateStatusRecurringSchedule(RecurringSchedule schedule, ScheduleStatus newStatus) {
        if (schedule == null || newStatus == null) {
            throw new IllegalArgumentException("Schedule and status must not be null.");
        }

        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            return false;
        }

        if (schedule.getScheduleStatus() == ScheduleStatus.PAUSED && newStatus == ScheduleStatus.ACTIVE) {
            LocalDate nextDate = calculateNextDate(schedule);
            schedule.setNextCollectionDate(nextDate);
            schedule.setScheduleStatus(ScheduleStatus.ACTIVE);
            recurringScheduleDAO.update(schedule);
            collectionManager.generateCollection(schedule);
            return true;
        }

        if (schedule.getScheduleStatus() == ScheduleStatus.ACTIVE &&
                (newStatus == ScheduleStatus.PAUSED || newStatus == ScheduleStatus.CANCELLED)) {
            schedule.setNextCollectionDate(null);
            schedule.setScheduleStatus(newStatus);
            recurringScheduleDAO.update(schedule);

            Collection associatedCollection = collectionManager.getActiveCollectionByRecurringSchedule(schedule);
            ValidateUtils.requireStateNotNull(associatedCollection, "Associated collection must not be null");
            collectionManager.softDeleteCollection(associatedCollection);
        }
        return true;
    }

    public boolean updateFrequency(RecurringSchedule schedule, Frequency newFrequency) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule must not be null");
        ValidateUtils.requireArgNotNull(newFrequency, "Frequency must not be null");

        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            return false;
        }

        if (schedule.getFrequency() == newFrequency) {
            return false;
        }

        LocalDate oldNextDate = schedule.getNextCollectionDate();
        LocalDate newNextDate = calculateFirstDate(schedule);

        if (!newNextDate.equals(oldNextDate)) {
            schedule.setNextCollectionDate(newNextDate);
            schedule.setFrequency(newFrequency);
            recurringScheduleDAO.update(schedule);

            Collection activeCollection = collectionManager.getActiveCollectionByRecurringSchedule(schedule);
            if (activeCollection != null) {
                collectionManager.softDeleteCollection(activeCollection);
            }
            collectionManager.generateCollection(schedule);
            return true;
        }

        return false;
    }

}
