package it.unibo.wastemaster.core.services;

import java.util.Date;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.OneTimeSchedule;

public class CollectionManager {
    private GenericDAO<Collection> collectionDAO;

    public CollectionManager(GenericDAO<Collection> collectionDAO) {
        this.collectionDAO = collectionDAO;
    }    

    public void generateOneTimeCollection(OneTimeSchedule schedule) {
        if (schedule != null && schedule.getPickupDate() != null) {
            Date pickupDate = schedule.getPickupDate();
            if (pickupDate.after(new Date())) {
                Collection collection = new Collection(
                        schedule.getCustomer(),
                        schedule.getPickupDate(),
                        schedule.getWasteType(),
                        Collection.CollectionStatus.IN_PROGRESS,
                        2,
                        schedule,
                        Collection.ScheduleCategory.ONE_TIME);
                collectionDAO.insert(collection);
            }
        }
    }

}
