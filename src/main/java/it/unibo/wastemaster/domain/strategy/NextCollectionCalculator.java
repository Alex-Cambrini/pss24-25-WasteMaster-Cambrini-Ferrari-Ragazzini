package it.unibo.wastemaster.domain.strategy;

import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.LocalDate;

public interface NextCollectionCalculator {

    /**
     * Calculates the next collection date for the recurring schedule
     * based on the waste schedule.
     *
     * @param schedule the recurring schedule
     * @param wasteSchedule the associated waste schedule
     * @return the next collection date
     */
    LocalDate calculateNextDate(RecurringSchedule schedule, WasteSchedule wasteSchedule);
}
