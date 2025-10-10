package it.unibo.wastemaster.domain.factory;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;

/**
 * Factory implementation for creating Collection instances.
 */
public class CollectionFactoryImpl implements CollectionFactory {

    /**
     * Creates a Collection with a recurring schedule.
     *
     * @param schedule the recurring schedule for the collection
     * @return a Collection instance with the given recurring schedule
     */
    @Override
    public Collection createRecurringCollection(final RecurringSchedule schedule) {
        return new Collection(schedule);
    }

    /**
     * Creates a Collection with a one-time schedule.
     *
     * @param schedule the one-time schedule for the collection
     * @return a Collection instance with the given one-time schedule
     */
    @Override
    public Collection createOneTimeCollection(final OneTimeSchedule schedule) {
        return new Collection(schedule);
    }
}
