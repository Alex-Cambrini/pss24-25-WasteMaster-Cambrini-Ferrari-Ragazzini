package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.dao.CollectionDAO;
import it.unibo.wastemaster.core.dao.GenericDAO;
import it.unibo.wastemaster.core.dao.RecurringScheduleDAO;
import it.unibo.wastemaster.core.dao.WasteScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;


class RecurringScheduleManagerTest extends AbstractDatabaseTest {

    WasteScheduleDAO wasteScheduleDAO = new WasteScheduleDAO(em);
	RecurringScheduleDAO recurringScheduleDAO = new RecurringScheduleDAO(em);
	WasteScheduleManager wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
	CollectionDAO collectionDAO = new CollectionDAO(em);
    CollectionManager collectionManager = new CollectionManager(collectionDAO, null);
	RecurringScheduleManager manager = new RecurringScheduleManager(recurringScheduleDAO, wasteScheduleManager, collectionManager);

    @Test
	void testCalculateNextDate() {

		Waste waste = new Waste(Waste.WasteType.PLASTIC, true, false);
		em.getTransaction().begin();
		em.persist(waste);
		em.persist(customer);
		em.getTransaction().commit();

		int scheduledDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		wasteScheduleDAO.insert(new WasteSchedule(waste, scheduledDay));

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
}


    @Test
    void testCreateReservationExtra() {
        em.getTransaction().begin();

        Customer customer = new Customer();
        customer.setName("Mario Rossi");
        em.persist(customer);

        Date date = new Date();
        Waste.WasteType wasteType = Waste.WasteType.PLASTIC;

        ReservationExtra reservation = manager.createReservationExtra(customer, date, wasteType);

        em.getTransaction().commit();

        assertNotNull(reservation.getReservationId());
        assertEquals(ReservationExtra.ReservationStatus.PENDING, reservation.getStatus());
        assertNotNull(reservation.getCollection());
        assertEquals(Collection.ScheduleType.EXTRA, reservation.getCollection().getScheduleType());
    }
}

