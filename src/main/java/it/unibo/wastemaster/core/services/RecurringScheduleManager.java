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
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start Date must be today or in the future");
        }
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

        if (schedule.getNextCollectionDate() == null) {
            return calculateFirstDate(schedule);
        } else {
            return calculateRecurringDate(schedule);
        }
    }

    private LocalDate calculateFirstDate(RecurringSchedule schedule) {
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getStartDate().plusDays(2);

        return alignToScheduledDay(date, scheduleData.getDayOfWeek());
    }

    private LocalDate calculateRecurringDate(RecurringSchedule schedule) {
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getNextCollectionDate();
        LocalDate today = dateUtils.getCurrentDate();
        do {
            if (schedule.getFrequency() == RecurringSchedule.Frequency.WEEKLY) {
                date = date.plusWeeks(1);
            } else {
                date = date.plusMonths(1);
            }
            date = alignToScheduledDay(date, scheduleData.getDayOfWeek());
        } while (!date.isAfter(today));

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
        ValidateUtils.requireArgNotNull(schedule, "Schedule must not be null");
        ValidateUtils.requireArgNotNull(newStatus, "Status must not be null");

        System.err.println(schedule);
        ScheduleStatus currentStatus = schedule.getScheduleStatus();

        if (currentStatus == ScheduleStatus.CANCELLED || currentStatus == ScheduleStatus.COMPLETED) {
            return false;
        }

        switch (currentStatus) {
            case PAUSED -> {
                if (newStatus == ScheduleStatus.CANCELLED) {
                    schedule.setScheduleStatus(ScheduleStatus.CANCELLED);
                    recurringScheduleDAO.update(schedule);
                    return true;
                }
                if (newStatus == ScheduleStatus.ACTIVE) {
                    LocalDate today = dateUtils.getCurrentDate();
                    LocalDate nextDate = schedule.getNextCollectionDate();
                    if (nextDate != null && !nextDate.isBefore(today)) {
                        nextDate = schedule.getNextCollectionDate();
                    } else {
                        nextDate = calculateNextDate(schedule);
                    }
                    schedule.setNextCollectionDate(nextDate);
                    schedule.setScheduleStatus(ScheduleStatus.ACTIVE);
                    recurringScheduleDAO.update(schedule);
                    collectionManager.generateCollection(schedule);
                    return true;
                }
                return false;
            }
            case ACTIVE -> {
                if (newStatus == ScheduleStatus.PAUSED || newStatus == ScheduleStatus.CANCELLED) {
                    ;
                    schedule.setScheduleStatus(newStatus);
                    recurringScheduleDAO.update(schedule);

                    Collection associatedCollection = collectionManager
                            .getActiveCollectionByRecurringSchedule(schedule);
                    ValidateUtils.requireStateNotNull(associatedCollection, "Associated collection must not be null");
                    collectionManager.softDeleteCollection(associatedCollection);
                    return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean updateFrequency(RecurringSchedule schedule, Frequency newFrequency) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule must not be null");
        ValidateUtils.requireArgNotNull(newFrequency, "Frequency must not be null");

        if (schedule.getScheduleStatus() != ScheduleStatus.ACTIVE) {
            return false;
        }

        if (schedule.getFrequency() == newFrequency) {
            return false;
        }

        LocalDate oldNextDate = schedule.getNextCollectionDate();
        schedule.setFrequency(newFrequency);
        LocalDate newNextDate = calculateFirstDate(schedule);

        if (!newNextDate.equals(oldNextDate)) {
            schedule.setNextCollectionDate(newNextDate);
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
