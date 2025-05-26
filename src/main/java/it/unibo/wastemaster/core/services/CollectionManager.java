package it.unibo.wastemaster.core.services;

import java.util.List;

import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;

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

    public List<Collection> getAllCollectionBySchedule(Schedule schedule) {
        return collectionDAO.findAllCollectionsBySchedule(schedule);
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

    public boolean softDeleteCollection(Collection collection) {
        ValidateUtils.requireArgNotNull(collection, "Collection cannot be null");
        ValidateUtils.requireArgNotNull(collection.getCollectionId(), "Collection ID cannot be null");

        if (collection.getCollectionStatus() == CollectionStatus.CANCELLED) {
            return false;
        }
        collection.setCollectionStatus(CollectionStatus.CANCELLED);
        updateCollection(collection);
        return true;
    }

    public void updateCollection(Collection collection) {
        collectionDAO.update(collection);
    }

    public Collection getActiveCollectionByRecurringSchedule(RecurringSchedule schedule) {
        return collectionDAO.findActiveCollectionByRecurringSchedule(schedule);
    }

}