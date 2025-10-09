package it.unibo.wastemaster.domain.strategy;

import static it.unibo.wastemaster.domain.service.RecurringScheduleManager.alignToScheduledDay;

import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.LocalDate;

/**
 * Calculates the next collection date for a monthly recurring schedule.
 * If no next collection date exists, calculation starts from startDate + 2 days.
 * The resulting date is always aligned to the day of the week specified in the
 * associated WasteSchedule.
 */
public class MonthlyCalculator implements NextCollectionCalculator {

    /**
     * Returns the next collection date for the given schedule.
     *
     * @param schedule the recurring schedule to process, must not be null
     * @param wasteSchedule the associated waste schedule, must not be null
     * @return the next collection date, aligned to the scheduled day of the week
     */
    @Override
    public LocalDate calculateNextDate(final RecurringSchedule schedule,
                                       final WasteSchedule wasteSchedule) {
        LocalDate date;
        if (schedule.getNextCollectionDate() == null) {
            date = schedule.getStartDate().plusDays(2);
        } else {
            date = schedule.getNextCollectionDate().plusMonths(1);
        }

        return alignToScheduledDay(date, wasteSchedule.getDayOfWeek());
    }
}
