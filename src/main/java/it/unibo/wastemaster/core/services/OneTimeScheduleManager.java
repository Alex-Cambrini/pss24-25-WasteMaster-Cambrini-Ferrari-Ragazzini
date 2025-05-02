package it.unibo.wastemaster.core.services;

import java.time.LocalDate;

import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;

public class OneTimeScheduleManager {

    private final OneTimeScheduleDAO oneTimeScheduleDAO;
    private final CollectionManager collectionManager;

    public OneTimeScheduleManager(OneTimeScheduleDAO oneTimeScheduleDAO, CollectionManager collectionManager) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
        this.collectionManager = collectionManager;
    }

    public void createOneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status,
            LocalDate pickupDate) {
        if (!isDateValid(pickupDate, Collection.CANCEL_LIMIT_DAYS)) {
            throw new IllegalArgumentException(
                    "The pickup date must be at least " + Collection.CANCEL_LIMIT_DAYS + " days from now.");
        }

        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, pickupDate);
        schedule.setStatus(status);
        oneTimeScheduleDAO.insert(schedule);
        collectionManager.generateOneTimeCollection(schedule);
    }

    private boolean isDateValid(LocalDate date, int limitDays) {
        LocalDate today = new DateUtils().getCurrentDate();
        return today.isBefore(date.minusDays(limitDays));
    }

    public boolean updateDateOneTimeSchedule(OneTimeSchedule schedule, LocalDate newPickupDate) {
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(schedule.getScheduleId());
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

    public boolean updateWasteTypeOneTimeSchedule(OneTimeSchedule schedule, Waste.WasteType wasteType) {
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(schedule.getScheduleId());
        if (collection == null)
            return false;

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setWasteType(wasteType);
            oneTimeScheduleDAO.update(schedule);

            collection.setWaste(wasteType);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }

    public boolean cancelOneTimeSchedule(OneTimeSchedule schedule) {
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(schedule.getScheduleId());
        if (collection == null)
            return false;

        if (isDateValid(schedule.getPickupDate(), collection.getCancelLimitDays())) {
            schedule.setStatus(ScheduleStatus.CANCELLED);
            oneTimeScheduleDAO.update(schedule);

            collection.setCollectionStatus(CollectionStatus.CANCELLED);
            collectionManager.updateCollection(collection);
            return true;
        }
        return false;
    }
}
