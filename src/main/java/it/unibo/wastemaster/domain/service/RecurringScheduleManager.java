package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule.Frequency;
import it.unibo.wastemaster.domain.model.Schedule.ScheduleStatus;
import it.unibo.wastemaster.domain.repository.RecurringScheduleRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Manages recurring schedules: creation, update, and retrieval. Works with DAO,
 * WasteScheduleManager, and CollectionManager.
 */
public class RecurringScheduleManager {

    private static final String SCHEDULE_NOT_NULL_MSG = "Schedule must not be null";

    private final RecurringScheduleRepository recurringScheduleRepository;
    private final WasteScheduleManager wasteScheduleManager;
    private CollectionManager collectionManager;

    /**
     * Constructor.
     *
     * @param recurringScheduleRepository DAO for recurring schedules, must not be null
     * @param wasteScheduleManager Manager for waste schedules, must not be null
     */
    public RecurringScheduleManager(
            final RecurringScheduleRepository recurringScheduleRepository,
            final WasteScheduleManager wasteScheduleManager) {
        this.wasteScheduleManager = wasteScheduleManager;
        this.recurringScheduleRepository = recurringScheduleRepository;
    }

    /**
     * Sets the collection manager.
     *
     * @param collectionManager the collection manager to set, must not be null
     */
    public void setCollectionManager(final CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Creates a new recurring schedule.
     *
     * @param customer the customer, must not be null
     * @param waste the waste type, must not be null
     * @param startDate the start date, must be today or later
     * @param frequency the frequency, must not be null
     * @return the created RecurringSchedule
     * @throws IllegalArgumentException if startDate is before today
     */
    public RecurringSchedule createRecurringSchedule(final Customer customer,
            final Waste waste, final LocalDate startDate, final Frequency frequency) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Start Date must be today or in the future");
        }
        RecurringSchedule schedule =
                new RecurringSchedule(customer, waste, startDate, frequency);
        LocalDate nextCollectionDate = calculateNextDate(schedule);
        schedule.setNextCollectionDate(nextCollectionDate);
        recurringScheduleRepository.save(schedule);
        collectionManager.generateCollection(schedule);
        return schedule;
    }

    private LocalDate calculateNextDate(final RecurringSchedule schedule) {
        ValidateUtils.requireArgNotNull(schedule, SCHEDULE_NOT_NULL_MSG);
        ValidateUtils.requireArgNotNull(schedule.getScheduleId(),
                "Schedule ID must not be null");

        if (schedule.getNextCollectionDate() == null) {
            return calculateFirstDate(schedule);
        } else {
            return calculateRecurringDate(schedule);
        }
    }

    private LocalDate calculateFirstDate(final RecurringSchedule schedule) {
        WasteSchedule scheduleData =
                wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getStartDate().plusDays(2);

        return alignToScheduledDay(date, scheduleData.getDayOfWeek());
    }

    private LocalDate calculateRecurringDate(final RecurringSchedule schedule) {
        WasteSchedule scheduleData =
                wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate date = schedule.getNextCollectionDate();
        LocalDate today = LocalDate.now();
        do {
            if (schedule.getFrequency() == RecurringSchedule.Frequency.WEEKLY) {
                date = date.plusWeeks(1);
            } else {
                date = date.plusMonths(1);
            }
            date = alignToScheduledDay(date, scheduleData.getDayOfWeek());
        } while (!date.isAfter(today));

        return date;
    }

    private LocalDate alignToScheduledDay(final LocalDate date,
            final DayOfWeek scheduledDay) {
        LocalDate adjustedDate = date;
        while (adjustedDate.getDayOfWeek() != scheduledDay) {
            adjustedDate = adjustedDate.plusDays(1);
        }
        return adjustedDate;
    }

    /**
     * Returns a list of active recurring schedules without future collections.
     *
     * @return list of recurring schedules without future collections
     */
    public final List<RecurringSchedule> getRecurringSchedulesWithoutCollections() {
        return recurringScheduleRepository.findActiveSchedulesWithoutFutureCollections();
    }

    /**
     * Updates the next collection dates for active recurring schedules with next dates
     * before today, and triggers generation of recurring collections.
     */
    public final void updateNextDates() {
        List<RecurringSchedule> schedules =
                recurringScheduleRepository.findActiveSchedulesWithNextDateBeforeToday();
        for (RecurringSchedule schedule : schedules) {
            LocalDate nextDate = calculateNextDate(schedule);
            schedule.setNextCollectionDate(nextDate);
            recurringScheduleRepository.update(schedule);
        }
        collectionManager.generateRecurringCollections();
    }

    /**
     * Returns a list of recurring schedules for the given customer.
     *
     * @param customer the customer whose schedules are retrieved
     * @return list of recurring schedules for the specified customer
     */
    public final List<RecurringSchedule> getSchedulesByCustomer(final Customer customer) {
        return recurringScheduleRepository.findSchedulesByCustomer(customer);
    }

    /**
     * Updates the status of a given recurring schedule if allowed.
     * <p>
     * This method returns false if the current status is CANCELLED or COMPLETED. If the
     * current status is PAUSED, it can be changed to CANCELLED or ACTIVE. If the current
     * status is ACTIVE, it can be changed to PAUSED or CANCELLED. When switching to
     * ACTIVE, the method updates the next collection date and generates a new collection.
     * When switching from ACTIVE to PAUSED or CANCELLED, it soft deletes the associated
     * collection.
     *
     * @param schedule the recurring schedule to update (must not be null)
     * @param newStatus the new status to set (must not be null)
     * @return true if the status was successfully updated; false otherwise
     * @throws IllegalArgumentException if schedule or newStatus is null
     * @throws IllegalStateException if associated collection is missing when required
     */
    public final boolean updateStatusRecurringSchedule(final RecurringSchedule schedule,
            final ScheduleStatus newStatus) {
        ValidateUtils.requireArgNotNull(schedule, SCHEDULE_NOT_NULL_MSG);
        ValidateUtils.requireArgNotNull(newStatus, "Status must not be null");

        ScheduleStatus currentStatus = schedule.getScheduleStatus();

        if (currentStatus == ScheduleStatus.CANCELLED
                || currentStatus == ScheduleStatus.COMPLETED) {
            return false;
        }

        switch (currentStatus) {
            case PAUSED -> {
                if (newStatus == ScheduleStatus.CANCELLED) {
                    schedule.setScheduleStatus(ScheduleStatus.CANCELLED);
                    recurringScheduleRepository.update(schedule);
                    return true;
                }
                if (newStatus == ScheduleStatus.ACTIVE) {
                    LocalDate today = LocalDate.now();
                    LocalDate nextDate = schedule.getNextCollectionDate();
                    if (nextDate != null && !nextDate.isBefore(today)) {
                        nextDate = schedule.getNextCollectionDate();
                    } else {
                        nextDate = calculateNextDate(schedule);
                    }
                    schedule.setNextCollectionDate(nextDate);
                    schedule.setScheduleStatus(ScheduleStatus.ACTIVE);
                    recurringScheduleRepository.update(schedule);
                    collectionManager.generateCollection(schedule);
                    return true;
                }
                return false;
            }
            case ACTIVE -> {
                if (newStatus == ScheduleStatus.PAUSED
                        || newStatus == ScheduleStatus.CANCELLED) {
                    schedule.setScheduleStatus(newStatus);
                    recurringScheduleRepository.update(schedule);

                    Collection associatedCollection = collectionManager
                            .getActiveCollectionByRecurringSchedule(schedule)
                            .orElseThrow(() -> new IllegalStateException(
                                    "Associated collection must not be null"));

                    collectionManager.softDeleteCollection(associatedCollection);
                    return true;
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Updates the frequency of an active recurring schedule.
     * <p>
     * If the schedule is not active or the frequency is unchanged, returns false.
     * Otherwise, sets the new frequency, recalculates the next collection date, updates
     * the schedule, soft deletes any active collection, and generates a new collection.
     *
     * @param schedule the recurring schedule to update (must not be null)
     * @param newFrequency the new frequency to set (must not be null)
     * @return true if the frequency was updated; false otherwise
     * @throws IllegalArgumentException if schedule or newFrequency is null
     */
    public final boolean updateFrequency(final RecurringSchedule schedule,
            final Frequency newFrequency) {
        ValidateUtils.requireArgNotNull(schedule, SCHEDULE_NOT_NULL_MSG);
        ValidateUtils.requireArgNotNull(newFrequency, "Frequency must not be null");

        if (schedule.getScheduleStatus() != ScheduleStatus.ACTIVE) {
            return false;
        }

        if (schedule.getFrequency() == newFrequency) {
            return false;
        }

        schedule.setFrequency(newFrequency);

        LocalDate restartDate = LocalDate.now().plusDays(2);
        WasteSchedule wasteSchedule =
                wasteScheduleManager.getWasteScheduleByWaste(schedule.getWaste());
        LocalDate newNextDate =
                alignToScheduledDay(restartDate, wasteSchedule.getDayOfWeek());

        if (!newNextDate.equals(schedule.getNextCollectionDate())) {
            schedule.setNextCollectionDate(newNextDate);
        }

        recurringScheduleRepository.update(schedule);

        Optional<Collection> activeCollectionOpt =
                collectionManager.getActiveCollectionByRecurringSchedule(schedule);
        if (activeCollectionOpt.isPresent()) {
            collectionManager.softDeleteCollection(activeCollectionOpt.get());
        }
        collectionManager.generateCollection(schedule);

        return true;
    }

}
