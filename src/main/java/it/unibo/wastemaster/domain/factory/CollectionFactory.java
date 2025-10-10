package it.unibo.wastemaster.domain.factory;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;

/**
 * Factory for creating Collection instances with different schedules.
 */
public interface CollectionFactory {

    /**
     * Creates a Collection with a recurring schedule.
     *
     * @param schedule the recurring schedule
     * @return a Collection instance with the given recurring schedule
     */
    Collection createRecurringCollection(RecurringSchedule schedule);

    /**
     * Creates a Collection with a one-time schedule.
     *
     * @param schedule the one-time schedule
     * @return a Collection instance with the given one-time schedule
     */
    Collection createOneTimeCollection(OneTimeSchedule schedule);
}
