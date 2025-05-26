package it.unibo.wastemaster.core.services;

import java.time.LocalDate;

import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;

public class OneTimeScheduleManager {

    private final OneTimeScheduleDAO oneTimeScheduleDAO;
    private final CollectionManager collectionManager;

    public OneTimeScheduleManager(OneTimeScheduleDAO oneTimeScheduleDAO, CollectionManager collectionManager) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
        this.collectionManager = collectionManager;
    }

    public OneTimeSchedule createOneTimeSchedule(Customer customer, Waste waste, LocalDate pickupDate) {
        if (!isDateValid(pickupDate, Collection.CANCEL_LIMIT_DAYS)) {
            throw new IllegalArgumentException(
                    "The pickup date must be at least " + Collection.CANCEL_LIMIT_DAYS + " days from now.");
        }
        OneTimeSchedule schedule = new OneTimeSchedule(customer, waste, pickupDate);
        oneTimeScheduleDAO.insert(schedule);
        collectionManager.generateOneTimeCollection(schedule);
        return schedule;
    }

    private boolean isDateValid(LocalDate date, int limitDays) {
        LocalDate minDate = new DateUtils().getCurrentDate().plusDays(limitDays);
        return !date.isBefore(minDate);
    }

    public boolean softDeleteOneTimeSchedule(OneTimeSchedule schedule) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule cannot be null");
        ValidateUtils.requireArgNotNull(schedule.getScheduleId(), "Schedule ID cannot be null");

        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            return false;
        }

        Collection collection = collectionManager.getAllCollectionBySchedule(schedule).get(0);
        ValidateUtils.requireArgNotNull(collection, "Associated collection not found");

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setScheduleStatus(ScheduleStatus.CANCELLED);
            oneTimeScheduleDAO.update(schedule);
            collection.setCollectionStatus(CollectionStatus.CANCELLED);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }

}
