package it.unibo.wastemaster.core.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.utils.DateUtils;

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

    public void createRecurringSchedule(Customer customer, Waste.WasteType wasteType, LocalDate startDate,
            Frequency frequency) {
        RecurringSchedule schedule = new RecurringSchedule(customer, wasteType, startDate, frequency);
        LocalDate nextCollectionDate = calculateNextDate(schedule);
        schedule.setNextCollectionDate(nextCollectionDate);
        recurringScheduleDAO.insert(schedule);
        collectionManager.generateCollection(schedule);
    }

    protected LocalDate calculateNextDate(RecurringSchedule schedule) {
        Waste.WasteType wasteType = schedule.getWasteType();
        RecurringSchedule.Frequency frequency = schedule.getFrequency();
        WasteSchedule scheduleData = wasteScheduleManager.getWasteScheduleForWaste(wasteType);
        DayOfWeek scheduledDay = scheduleData.getDayOfWeek();

        LocalDate today = dateUtils.getCurrentDate();
        LocalDate existingNext = schedule.getNextCollectionDate();
        LocalDate nextDate = null;

        // Se la data esistente è null, significa che è il primo ritiro
        if (existingNext == null) {
            nextDate = schedule.getStartDate().plusDays(2); // Aggiungi 2 giorni alla start date per il primo ritiro
            nextDate = alignToScheduledDay(nextDate, scheduledDay); // Allineamento iniziale
        } else { // significa che non è il primo ritiro
            nextDate = existingNext;
            // Aggiungi la frequenza (settimanale o mensile)
            if (frequency == RecurringSchedule.Frequency.WEEKLY) {
                nextDate = nextDate.plusWeeks(1); // Aggiungi 7 giorni per la frequenza settimanale
            } else if (frequency == RecurringSchedule.Frequency.MONTHLY) {
                nextDate = nextDate.plusMonths(1); // Aggiungi 1 mese per la frequenza mensile
                nextDate = alignToScheduledDay(nextDate, scheduledDay); // Allineamento per frequenza mensile
            }

            // Se la data risultante è nel passato, scorrere giorno per giorno
            while (nextDate.isBefore(today)) {
                nextDate = nextDate.plusDays(1); // Incrementa di un giorno
                nextDate = alignToScheduledDay(nextDate, scheduledDay); // Allineamento per trovare il giorno giusto
            }
        }

        return nextDate;
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
}