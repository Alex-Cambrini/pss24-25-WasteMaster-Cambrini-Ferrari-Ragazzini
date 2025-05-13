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
        LocalDate today = new DateUtils().getCurrentDate();
        return today.isBefore(date.minusDays(limitDays));
    }

    public boolean updateDateOneTimeSchedule(OneTimeSchedule schedule, LocalDate newPickupDate) {
        Collection collection = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
        if (collection == null)
            return false;

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setPickupDate(newPickupDate);
            oneTimeScheduleDAO.update(schedule);

            collection.setCollectionDate(newPickupDate);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }

    public boolean updateWasteOneTimeSchedule(OneTimeSchedule schedule, Waste Waste) {
        Collection collection = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
        if (collection == null)
            return false;

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setWaste(Waste);
            oneTimeScheduleDAO.update(schedule);

            collection.setWaste(Waste);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }

    public boolean updateStatusOneTimeSchedule(OneTimeSchedule schedule, ScheduleStatus newStatus) {
        ValidateUtils.requireArgNotNull(schedule, "Schedule cannot be null");
        ValidateUtils.requireArgNotNull(newStatus, "New status cannot be null");
        ValidateUtils.requireArgNotNull(schedule.getScheduleId(), "Schedule ID cannot be null");       
    
        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            return false;
        }
    
        if (schedule.getScheduleStatus() == ScheduleStatus.PAUSED && newStatus == ScheduleStatus.ACTIVE) {
            collectionManager.generateOneTimeCollection(schedule);
            return true;
        }
    
        if (schedule.getScheduleStatus() == ScheduleStatus.ACTIVE
            && (newStatus == ScheduleStatus.PAUSED || newStatus == ScheduleStatus.CANCELLED)) {
    
            Collection associatedCollection  = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
            ValidateUtils.requireArgNotNull(associatedCollection, "Associated collection not found");
    
            if (isDateValid(schedule.getPickupDate(), associatedCollection .getCancelLimitDays())) {
                schedule.setScheduleStatus(newStatus);
                oneTimeScheduleDAO.update(schedule);
                associatedCollection.setCollectionStatus(CollectionStatus.CANCELLED);
                collectionManager.updateCollection(associatedCollection );
                return true;
            }
        }    
        return false;
    }
    
}
