package it.unibo.wastemaster.core.services;

import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.Collection.ScheduleCategory;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.utils.DateUtils;

public class CollectionManager {

    private RecurringScheduleManager recurringScheduleManager;
    private CollectionDAO collectionDAO;

    public CollectionManager(CollectionDAO collectionDAO, RecurringScheduleManager recurringScheduleManager) {
        this.collectionDAO = collectionDAO;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    public List<Collection> getCollectionsByStatus(CollectionStatus status) {
        return collectionDAO.findCollectionByStatus(status);
    }

    public void generateCollection(Schedule schedule) {
        LocalDate collectionDate = null;
        ScheduleCategory scheduleCategory = null;
        Collection.CollectionStatus collectionStatus = Collection.CollectionStatus.IN_PROGRESS;

        if (schedule instanceof OneTimeSchedule) {
            collectionDate = ((OneTimeSchedule) schedule).getPickupDate();
            scheduleCategory = ScheduleCategory.ONE_TIME;
        } else if (schedule instanceof RecurringSchedule) {
            collectionDate = ((RecurringSchedule) schedule).getNextCollectionDate();
            scheduleCategory = ScheduleCategory.RECURRING;
        }

        if (collectionDate != null && collectionDate.isAfter(DateUtils.getCurrentDate())) {
            Collection collection = new Collection(
                    schedule.getCustomer(),
                    collectionDate,
                    schedule.getWasteType(),
                    collectionStatus,
                    schedule,
                    scheduleCategory);
            collectionDAO.insert(collection);
        }
    }

    public void generateOneTimeCollection(OneTimeSchedule schedule) {
        generateCollection(schedule);
    }

    public void generateRecurringCollections() {
        List<RecurringSchedule> upcomingSchedules = recurringScheduleManager.getRecurringSchedulesWithoutCollections();
        for (RecurringSchedule schedule : upcomingSchedules) {
            generateCollection(schedule);
        }
    }

    public void updateCollection(Collection collection) {
        collectionDAO.update(collection);
    }
}