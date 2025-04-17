package it.unibo.wastemaster.core.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

public class RecurringScheduleManager {

    private RecurringScheduleDAO recurringScheduleDAO;
    private WasteScheduleManager wasteScheduleManager;
    private CollectionManager collectionManager;

    public RecurringScheduleManager(RecurringScheduleDAO recurringScheduleDAO,
            WasteScheduleManager wasteScheduleManager, CollectionManager collectionManager) {
        this.wasteScheduleManager = wasteScheduleManager;
        this.recurringScheduleDAO = recurringScheduleDAO;
        this.collectionManager = collectionManager;
    }

    public void createRecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status,
            Date startDate,
            Frequency frequency) {
        RecurringSchedule schedule = new RecurringSchedule(customer, wasteType, status, startDate, frequency);
        Date nextCollectionDate = calculateNextDate(schedule);
        schedule.setNextCollectionDate(nextCollectionDate);
        recurringScheduleDAO.insert(schedule);
        collectionManager.generateCollection(schedule);
    }

    protected Date calculateNextDate(RecurringSchedule schedule) {
        Waste.WasteType wasteType = schedule.getWasteType();
        RecurringSchedule.Frequency frequency = schedule.getFrequency();
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleForWaste(wasteType);
        int scheduledDay = scheduleData.getDayOfWeek();

        Date today = new Date();
        Date existingNext = schedule.getNextCollectionDate();

        if (existingNext != null && !existingNext.before(today)) {
            return existingNext;
        }

        Calendar calendar = Calendar.getInstance();

        if (existingNext == null) {
            calendar.setTime(schedule.getStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, Collection.CANCEL_LIMIT_DAYS);
        } else {
            calendar.setTime(existingNext);
        }
        if (existingNext != null) {
            calendar = alignToScheduledDay(calendar, scheduledDay);
            while (calendar.getTime().before(today)) {
                if (frequency == RecurringSchedule.Frequency.WEEKLY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                } else if (frequency == RecurringSchedule.Frequency.MONTHLY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 28);
                }
                calendar = alignToScheduledDay(calendar, scheduledDay);
            }
        }
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    protected Calendar alignToScheduledDay(Calendar calendar, int scheduledDay) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != scheduledDay) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return calendar;
    }

    public List<RecurringSchedule> getRecurringSchedulesWithoutCollections() {
        return recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();
    }

    public void updateNextDates() {
        List<RecurringSchedule> schedules = recurringScheduleDAO.findActiveSchedulesWithNextDateBeforeToday();
        for (RecurringSchedule schedule : schedules) {
            Date nextDate = calculateNextDate(schedule);
            schedule.setNextCollectionDate(nextDate);
            recurringScheduleDAO.update(schedule);
        }
        collectionManager.generateRecurringCollections();
    }
}