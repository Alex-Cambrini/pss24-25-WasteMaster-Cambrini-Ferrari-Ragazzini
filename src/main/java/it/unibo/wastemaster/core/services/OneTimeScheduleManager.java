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
    private CollectionManager collectionManager;

    public OneTimeScheduleManager(OneTimeScheduleDAO oneTimeScheduleDAO, CollectionManager collectionManager) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
        this.collectionManager = collectionManager;
    }

    public void createOneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status, LocalDate pickupDate) {
         
        if (!isDateValid(pickupDate, Collection.CANCEL_LIMIT_DAYS)) {
            throw new IllegalArgumentException("La data di ritiro deve essere almeno tra " + Collection.CANCEL_LIMIT_DAYS + " giorni.");
        }    
        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, status, pickupDate);
        oneTimeScheduleDAO.insert(schedule);
        collectionManager.generateOneTimeCollection(schedule);
    }

    private boolean isDateValid(LocalDate date, int limitDays) {
        LocalDate today = DateUtils.getCurrentDate();
        LocalDate cancelLimitDate = date.minusDays(limitDays);
        return today.isBefore(cancelLimitDate);
    }
    

    public boolean updateDateOneTimeSchedule(OneTimeSchedule schedule, LocalDate newPickupDate) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (isDateValid(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setPickupDate(newPickupDate);
            oneTimeScheduleDAO.update(schedule);

            collection.setDate(newPickupDate);
            collectionManager.updateCollection(collection);
            return true;
        }

        return false;
    }

    public boolean updateWasteTypeOneTimeSchedule(OneTimeSchedule schedule, Waste.WasteType wasteType) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (isDateValid(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setWasteType(wasteType);
            oneTimeScheduleDAO.update(schedule);

            collection.setWaste(wasteType);
            collectionManager.updateCollection(collection);
            return true;
        }

        return false;
    }

    public boolean cancelOneTimeSchedule(OneTimeSchedule schedule) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (isDateValid(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setStatus(ScheduleStatus.CANCELLED);
            oneTimeScheduleDAO.update(schedule);
            collection.setCollectionStatus(CollectionStatus.CANCELLED);
            collectionManager.updateCollection(collection);     
            return true;
        }

        return false;
    }

}
