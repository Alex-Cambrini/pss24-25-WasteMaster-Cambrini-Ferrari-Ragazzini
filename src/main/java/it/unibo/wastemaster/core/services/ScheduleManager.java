package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

import java.util.Calendar;
import java.util.Date;

public class ScheduleManager {

    private WasteScheduleManager wasteScheduleManager;
    private GenericDAO<RecurringSchedule> recurringScheduleDAO;
    private GenericDAO<OneTimeSchedule> oneTimeScheduleDAO;

    public ScheduleManager(WasteScheduleManager wasteScheduleManager, GenericDAO<RecurringSchedule> recurringScheduleDAO, GenericDAO<OneTimeSchedule> oneTimeScheduleDAO) {
        this.wasteScheduleManager = wasteScheduleManager;
        this.recurringScheduleDAO = recurringScheduleDAO;
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
    }

    public void createOneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Date pickupDate) {
        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, status, pickupDate);
        oneTimeScheduleDAO.insert(schedule);
    }

    public void createRecurringSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, Frequency frequency) {
        RecurringSchedule schedule = new RecurringSchedule(customer, wasteType, status, frequency);
        Date nextCollectionDate = calculateNextDate(schedule);        
        schedule.setNextCollectionDate(nextCollectionDate);
        recurringScheduleDAO.insert(schedule);
    }

    private Date calculateNextDate(RecurringSchedule schedule) {
        Waste.WasteType wasteType = schedule.getWasteType();
        RecurringSchedule.Frequency frequency = schedule.getFrequency();
        Date currentDate = schedule.getNextCollectionDate();
        
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleForWaste(wasteType);
        int scheduledDay = scheduleData.getDayOfWeek(); // 1 = domenica ... 7 = sabato

        Calendar calendar = Calendar.getInstance();
        if (currentDate == null) {
            calendar.setTime(new java.util.Date());
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.setTime(currentDate);
        }

        if (frequency == RecurringSchedule.Frequency.WEEKLY) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } else if (frequency == RecurringSchedule.Frequency.MONTHLY) {
            calendar.add(Calendar.DAY_OF_MONTH, 28);
        }

        while (calendar.get(Calendar.DAY_OF_WEEK) != scheduledDay) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }
}

