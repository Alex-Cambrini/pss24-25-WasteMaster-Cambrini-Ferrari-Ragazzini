package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Waste;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


class RecurringScheduleManagerTest extends AbstractDatabaseTest {

    WasteScheduleDAO wasteScheduleDAO = new WasteScheduleDAO(em);
	RecurringScheduleDAO recurringScheduleDAO = new RecurringScheduleDAO(em);
	WasteScheduleManager wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
	CollectionDAO collectionDAO = new CollectionDAO(em);
    CollectionManager collectionManager = new CollectionManager(collectionDAO, null);
	RecurringScheduleManager manager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager, collectionManager);

    @Test
    void testCalculateNextDate() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -5);
        RecurringSchedule scheduleNull = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, ScheduleStatus.ACTIVE, new java.sql.Date(start.getTimeInMillis()), Frequency.WEEKLY);
        scheduleNull.setNextCollectionDate(null);

        Date calculatedDate = manager.calculateNextDate(scheduleNull);
        assertNotNull(calculatedDate);

        RecurringSchedule scheduleToday = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, ScheduleStatus.ACTIVE, new Date(), Frequency.WEEKLY);
        scheduleToday.setNextCollectionDate(new java.sql.Date(System.currentTimeMillis()));

        Date resultToday = manager.calculateNextDate(scheduleToday);
        assertEquals(scheduleToday.getNextCollectionDate(), resultToday);

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 5);
        RecurringSchedule scheduleFuture = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, ScheduleStatus.ACTIVE, new Date(), Frequency.WEEKLY);
        scheduleFuture.setNextCollectionDate(new java.sql.Date(future.getTimeInMillis()));

        Date resultFuture = manager.calculateNextDate(scheduleFuture);
        assertEquals(scheduleFuture.getNextCollectionDate(), resultFuture);

        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -7);
        RecurringSchedule schedulePast = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, ScheduleStatus.ACTIVE, new Date(), Frequency.WEEKLY);
        schedulePast.setNextCollectionDate(new java.sql.Date(past.getTimeInMillis()));

        Date resultPast = manager.calculateNextDate(schedulePast);
        assertTrue(resultPast.after(new Date()));
    }

    @Test
    void testCreateRecurringSchedule() {
        Date startDate = new Date();

        manager.createRecurringSchedule(
            customer,
            Waste.WasteType.PLASTIC,
            ScheduleStatus.ACTIVE,
            startDate,
            Frequency.WEEKLY
        );

        List<RecurringSchedule> schedules = recurringScheduleDAO.findAll();
        assertEquals(1, schedules.size());

        RecurringSchedule schedule = schedules.get(0);
        assertEquals(customer.getCustomerId(), schedule.getCustomer().getCustomerId());
        assertEquals(Waste.WasteType.PLASTIC, schedule.getWasteType());
        assertEquals(ScheduleStatus.ACTIVE, schedule.getStatus());
        assertEquals(Frequency.WEEKLY, schedule.getFrequency());
        assertNotNull(schedule.getNextCollectionDate());

        List<Collection> collections = collectionDAO.findAll();
        assertEquals(1, collections.size());

        Collection collection = collections.get(0);
        assertEquals(customer.getCustomerId(), collection.getCustomer().getCustomerId());
        assertEquals(schedule.getNextCollectionDate(), collection.getDate());
        assertEquals(Collection.CollectionStatus.IN_PROGRESS, collection.getCollectionStatus());
    }


}

