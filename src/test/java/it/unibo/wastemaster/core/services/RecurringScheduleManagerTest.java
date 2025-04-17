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
import java.lang.reflect.Method;


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
        start.add(Calendar.DAY_OF_MONTH, -10);

        RecurringSchedule pastSchedule = new RecurringSchedule(
            customer,
            waste.getType(),
            ScheduleStatus.ACTIVE,
            new java.sql.Date(start.getTimeInMillis()),
            Frequency.WEEKLY
        );
        pastSchedule.setNextCollectionDate(new java.sql.Date(start.getTimeInMillis()));

        em.getTransaction().begin();
        em.persist(pastSchedule);
        em.getTransaction().commit();

        manager.updateNextDates();

        RecurringSchedule updated = em.find(RecurringSchedule.class, pastSchedule.getId());
        assertNotNull(updated.getNextCollectionDate());
        assertTrue(updated.getNextCollectionDate().after(new Date()));

        List<Collection> collections = collectionDAO.findAll();
        assertEquals(1, collections.size());

        Collection collection = collections.get(0);
        assertEquals(updated.getNextCollectionDate(), collection.getDate());
        assertEquals(customer.getCustomerId(), collection.getCustomer().getCustomerId());
        assertEquals(Collection.ScheduleCategory.RECURRING, collection.getScheduleCategory());
        assertEquals(updated.getId(), collection.getScheduleId().getId());

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

    @Test
    void testAlignToScheduledDay() throws Exception {
        Method method = RecurringScheduleManager.class.getDeclaredMethod("alignToScheduledDay", Calendar.class, int.class);
        method.setAccessible(true);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

        Calendar resultSame = (Calendar) method.invoke(manager, (Calendar) cal.clone(), Calendar.TUESDAY);
        assertEquals(Calendar.TUESDAY, resultSame.get(Calendar.DAY_OF_WEEK));

        Calendar resultNext = (Calendar) method.invoke(manager, (Calendar) cal.clone(), Calendar.FRIDAY);
        assertEquals(Calendar.FRIDAY, resultNext.get(Calendar.DAY_OF_WEEK));
        assertTrue(resultNext.after(cal));

        Calendar resultSunday = (Calendar) method.invoke(manager, (Calendar) cal.clone(), Calendar.SUNDAY);
        assertEquals(Calendar.SUNDAY, resultSunday.get(Calendar.DAY_OF_WEEK));

        Calendar endOfMonth = Calendar.getInstance();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        Calendar resultTransition = (Calendar) method.invoke(manager, endOfMonth, Calendar.TUESDAY);
        assertEquals(Calendar.TUESDAY, resultTransition.get(Calendar.DAY_OF_WEEK));
        assertTrue(resultTransition.get(Calendar.MONTH) != endOfMonth.get(Calendar.MONTH) || resultTransition.get(Calendar.DAY_OF_MONTH) != endOfMonth.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void testGetRecurringSchedulesWithoutCollections() {
        List<RecurringSchedule> result = manager.getRecurringSchedulesWithoutCollections();
        assertNotNull(result);
    }

    @Test
    void testUpdateNextDates() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -10);

        RecurringSchedule pastSchedule = new RecurringSchedule(customer, waste.getType(), ScheduleStatus.ACTIVE, new java.sql.Date(start.getTimeInMillis()), Frequency.WEEKLY);
        pastSchedule.setNextCollectionDate(new java.sql.Date(start.getTimeInMillis()));

        em.getTransaction().begin();
        em.persist(pastSchedule);
        em.getTransaction().commit();

        manager.updateNextDates();

        RecurringSchedule updated = em.find(RecurringSchedule.class, pastSchedule.getId());
        assertNotNull(updated.getNextCollectionDate());
        assertTrue(updated.getNextCollectionDate().after(new Date()));
    }

}

