package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleManager {

    private WasteScheduleManager wasteScheduleManager;
    private RecurringScheduleDAO recurringScheduleDAO;
    private GenericDAO<OneTimeSchedule> oneTimeScheduleDAO;

    public ScheduleManager(WasteScheduleManager wasteScheduleManager, RecurringScheduleDAO recurringScheduleDAO, GenericDAO<OneTimeSchedule> oneTimeScheduleDAO) {
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
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleForWaste(wasteType);
        int scheduledDay = scheduleData.getDayOfWeek();
    
        Calendar calendar = Calendar.getInstance();
    
        if (schedule.getNextCollectionDate() == null) {
            calendar.setTime(schedule.getCreationDate());
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.setTime(schedule.getNextCollectionDate());
        }
    
        while (calendar.get(Calendar.DAY_OF_WEEK) != scheduledDay) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    
        if (schedule.getNextCollectionDate() != null) {
            if (frequency == RecurringSchedule.Frequency.WEEKLY) {
                calendar.add(Calendar.DAY_OF_MONTH, 7);
            } else if (frequency == RecurringSchedule.Frequency.MONTHLY) {
                calendar.add(Calendar.DAY_OF_MONTH, 28);
            }
        }
    
        return new java.sql.Date(calendar.getTimeInMillis());
    }
 
    public List<RecurringSchedule> getRecurringSchedulesWithoutCollections() {
        return recurringScheduleDAO.findRecurringSchedulesWithoutFutureCollections();
    }
}

