package it.unibo.wastemaster.domain.strategy;

import static it.unibo.wastemaster.domain.service.RecurringScheduleManager.alignToScheduledDay;

import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.LocalDate;

public class MonthlyCalculator implements NextCollectionCalculator {

    @Override
    public LocalDate calculateNextDate(RecurringSchedule schedule,
                                       WasteSchedule wasteSchedule) {
        LocalDate date;
        if (schedule.getNextCollectionDate() == null) {
            date = schedule.getStartDate().plusDays(2);
        } else {
            date = schedule.getNextCollectionDate().plusMonths(1);
        }

        return alignToScheduledDay(date, wasteSchedule.getDayOfWeek());
    }
}
