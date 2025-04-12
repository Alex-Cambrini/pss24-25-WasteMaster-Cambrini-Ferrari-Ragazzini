package it.unibo.wastemaster.core.services;

import java.util.Date;
import java.util.List;

import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.ScheduleCategory;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;

public class CollectionManager {
    private GenericDAO<Collection> collectionDAO;
    private RecurringScheduleDAO recurringScheduleDAO;

    public CollectionManager(GenericDAO<Collection> collectionDAO, RecurringScheduleDAO recurringScheduleDAO) {
        this.collectionDAO = collectionDAO;
        this.recurringScheduleDAO = recurringScheduleDAO;
    }

    public void generateCollection(Schedule schedule) {
        Date collectionDate = null;
        ScheduleCategory scheduleCategory = null;
        Collection.CollectionStatus collectionStatus = Collection.CollectionStatus.IN_PROGRESS;
        int cancelLimit = 2;

        if (schedule instanceof OneTimeSchedule) {
            collectionDate = ((OneTimeSchedule) schedule).getPickupDate();
            scheduleCategory = ScheduleCategory.ONE_TIME;
        } else if (schedule instanceof RecurringSchedule) {
            collectionDate = ((RecurringSchedule) schedule).getNextCollectionDate();
            scheduleCategory = ScheduleCategory.RECURRING;
        }

        if (collectionDate != null && collectionDate.after(new Date())) {
            Collection collection = new Collection(
                    schedule.getCustomer(),
                    collectionDate,
                    schedule.getWasteType(),
                    collectionStatus,
                    cancelLimit,
                    schedule,
                    scheduleCategory);
            collectionDAO.insert(collection);
        }
    }
    
    public void generateOneTimeCollection(OneTimeSchedule schedule) {
        generateCollection(schedule);
    }

    public void generateRecurringCollections() {
        List<RecurringSchedule> upcomingSchedules = recurringScheduleDAO
                .findRecurringSchedulesWithoutFutureCollections();
        for (RecurringSchedule schedule : upcomingSchedules) {
            generateCollection(schedule);
        }
    }
}
