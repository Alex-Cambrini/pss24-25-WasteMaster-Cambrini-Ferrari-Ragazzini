package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.CollectionPlan;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

import java.util.Calendar;
import java.util.Date;

public class CollectionPlanService {

    private WasteScheduleManager wasteScheduleManager;

    public CollectionPlanService(WasteScheduleManager wasteScheduleManager) {
        this.wasteScheduleManager = wasteScheduleManager;
    }

    public Date calculateNextDate(CollectionPlan plan) {
        Waste.WasteType wasteType = plan.getWasteType();
        CollectionPlan.Frequency frequency = plan.getFrequency();
        Date currentDate = plan.getNextCollectionDate();
    
        WasteSchedule schedule = wasteScheduleManager.getWasteScheduleForWaste(wasteType);
        int scheduledDay = schedule.getDayOfWeek(); // 1 = domenica ... 7 = sabato
    
        Calendar calendar = Calendar.getInstance();
    
        // Se la data è null, significa che non è stato ancora programmato un ritiro
        // e quindi si calcola la data a partire da oggi + 2 giorni
        if (currentDate == null) {
            calendar.setTime(new java.util.Date());
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.setTime(currentDate);
        }
    
        // Se la frequenza è settimanale, si aggiungono 7 giorni
        if (frequency == CollectionPlan.Frequency.WEEKLY) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } 
        // Se la frequenza è mensile, si aggiungono 28 giorni
        else if (frequency == CollectionPlan.Frequency.MONTHLY) {
            calendar.add(Calendar.DAY_OF_MONTH, 28);
        }

        //Se non coincide con il giorno programmato, si cerca il primo giorno utile
        while (calendar.get(Calendar.DAY_OF_WEEK) != scheduledDay) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    
        return new java.sql.Date(calendar.getTimeInMillis());
    }
}