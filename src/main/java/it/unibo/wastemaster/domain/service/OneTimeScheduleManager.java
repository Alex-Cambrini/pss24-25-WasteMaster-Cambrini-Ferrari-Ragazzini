package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Schedule.ScheduleStatus;
import it.unibo.wastemaster.domain.repository.OneTimeScheduleRepository;
import java.time.LocalDate;

/**
 * Manages operations related to one-time waste pickup schedules.
 */
public class OneTimeScheduleManager {

    private final OneTimeScheduleRepository oneTimeScheduleRepository;
    private final CollectionManager collectionManager;

    /**
     * Constructs a OneTimeScheduleManager with required dependencies.
     *
     * @param oneTimeScheduleRepository the DAO for OneTimeSchedule persistence
     * @param collectionManager the manager handling related collection logic
     */
    public OneTimeScheduleManager(final OneTimeScheduleRepository oneTimeScheduleRepository,
                                  final CollectionManager collectionManager) {
        this.oneTimeScheduleRepository = oneTimeScheduleRepository;
        this.collectionManager = collectionManager;
    }

    /**
     * Creates a new one-time waste pickup schedule if the date is valid.
     *
     * @param customer the customer requesting the pickup
     * @param waste the waste to be collected
     * @param pickupDate the desired pickup date
     * @return the created OneTimeSchedule
     * @throws IllegalArgumentException if the pickup date is too soon
     */
    public OneTimeSchedule createOneTimeSchedule(final Customer customer,
                                                 final Waste waste,
                                                 final LocalDate pickupDate) {
        if (!isDateValid(pickupDate, Collection.CANCEL_LIMIT_DAYS)) {
            throw new IllegalArgumentException("The pickup date must be at least "
                    + Collection.CANCEL_LIMIT_DAYS + " days from now.");
        }
        final OneTimeSchedule schedule = new OneTimeSchedule(customer, waste, pickupDate);
        oneTimeScheduleRepository.save(schedule);
        collectionManager.generateOneTimeCollection(schedule);
        return schedule;
    }

    /**
     * Validates that the given date is at least a certain number of days in the future.
     *
     * @param date the date to validate
     * @param limitDays the minimum number of days required
     * @return true if the date is valid, false otherwise
     */
    private boolean isDateValid(final LocalDate date, final int limitDays) {
        final LocalDate minDate = LocalDate.now().plusDays(limitDays);
        return !date.isBefore(minDate);
    }

    /**
     * Attempts to cancel a one-time schedule and its associated collection, if the
     * cancellation is still allowed based on the pickup date.
     *
     * @param schedule the schedule to cancel
     * @return true if the schedule was successfully cancelled, false otherwise
     * @throws IllegalArgumentException if schedule or schedule ID is null
     */
    public boolean softDeleteOneTimeSchedule(final OneTimeSchedule schedule) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule cannot be null");
        ValidateUtils.requireArgNotNull(schedule.getScheduleId(),
                "Schedule ID cannot be null");

        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            return false;
        }

        final Collection collection =
                collectionManager.getAllCollectionBySchedule(schedule).get(0);
        ValidateUtils.requireArgNotNull(collection, "Associated collection not found");

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setScheduleStatus(ScheduleStatus.CANCELLED);
            oneTimeScheduleRepository.update(schedule);
            collection.setCollectionStatus(CollectionStatus.CANCELLED);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }
}
