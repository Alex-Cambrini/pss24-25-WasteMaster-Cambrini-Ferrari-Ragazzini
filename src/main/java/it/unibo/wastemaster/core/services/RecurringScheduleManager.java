package it.unibo.wastemaster.core.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.utils.DateUtils;

public class RecurringScheduleManager {

    private RecurringScheduleDAO recurringScheduleDAO;
    private WasteScheduleManager wasteScheduleManager;
    private CollectionManager collectionManager;

    public RecurringScheduleManager(RecurringScheduleDAO recurringScheduleDAO,
            WasteScheduleManager wasteScheduleManager) {
        this.wasteScheduleManager = wasteScheduleManager;
        this.recurringScheduleDAO = recurringScheduleDAO;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
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
    
        Date today = DateUtils.getCurrentDate();
        Date existingNext = schedule.getNextCollectionDate();
    
        Calendar calendar = Calendar.getInstance();
    
        // Se la data esistente è null, significa che è il primo ritiro
        if (existingNext == null) {
            calendar.setTime(schedule.getStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, 2); // Aggiungi 2 giorni alla start date per il primo ritiro
            calendar = alignToScheduledDay(calendar, scheduledDay); // Allineamento iniziale
        } else { // significa che non è il primo ritiro
            calendar.setTime(existingNext);    
            // Aggiungi la frequenza (settimanale o mensile)
            if (frequency == RecurringSchedule.Frequency.WEEKLY) {
                calendar.add(Calendar.DAY_OF_MONTH, 7); // Aggiungi 7 giorni per la frequenza settimanale
            } else if (frequency == RecurringSchedule.Frequency.MONTHLY) {
                calendar.add(Calendar.MONTH, 1); // Aggiungi 1 mese per la frequenza mensile
                calendar = alignToScheduledDay(calendar, scheduledDay); // Allineamento per frequenza mensile
            }
    
            // Se la data risultante è nel passato, scorrere giorno per giorno
            while (calendar.getTime().before(today)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1); // Incrementa di un giorno
                calendar = alignToScheduledDay(calendar, scheduledDay); // Allineamento per trovare il giorno giusto
            }
        }
    
        return new java.sql.Date(calendar.getTimeInMillis());
    }    
    
    private Calendar alignToScheduledDay(Calendar calendar, int scheduledDay) {
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

    public List<RecurringSchedule> getSchedulesByCustomer(Customer customer) {
        return recurringScheduleDAO.findScheduleByCustomer(customer);
    }
}