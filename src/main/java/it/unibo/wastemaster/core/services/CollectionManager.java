package it.unibo.wastemaster.core.services;

import java.util.List;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.utils.DateUtils;

public class CollectionManager {

    private RecurringScheduleManager recurringScheduleManager;
    private CollectionDAO collectionDAO;
    private DateUtils dateUtils = new DateUtils();

    public CollectionManager(CollectionDAO collectionDAO, RecurringScheduleManager recurringScheduleManager) {
        this.collectionDAO = collectionDAO;
        this.recurringScheduleManager = recurringScheduleManager;
    }

    public List<Collection> getCollectionsByStatus(CollectionStatus status) {
        return collectionDAO.findCollectionByStatus(status);
    }

    public Collection getActiveCollectionByOneTimeSchedule(OneTimeSchedule schedule) {
        return collectionDAO.findActiveCollectionByOneTimeSchedule(schedule);
    }

    public List<Collection> getCancelledCollectionsOneTimeSchedule(OneTimeSchedule schedule) {
        return collectionDAO.findCancelledCollectionsOneTimeSchedule(schedule);
    }

    public void generateCollection(Schedule schedule) {
        if (schedule.getCollectionDate().isAfter(dateUtils.getCurrentDate())) {
            Collection collection = new Collection(schedule);
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