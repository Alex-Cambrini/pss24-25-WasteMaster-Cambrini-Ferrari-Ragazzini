package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Waste;

public class RecurringScheduleDAOTest extends AbstractDatabaseTest {

    private Location location1;
    private Location location2;
    private Customer customer1;
    private Customer customer2;
    RecurringSchedule recurringSchedule1;
    RecurringSchedule recurringSchedule2;
    RecurringSchedule recurringSchedule3;
    RecurringSchedule recurringSchedule4;
    private LocalDate date;
    private Waste.WasteType wasteType;

    @BeforeEach
    public void setUp() {
        super.setUp();
        date = dateUtils.getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;

        location1 = new Location("Via Roma", "10", "Bologna", "40100");
        location2 = new Location("Via Milano", "32", "Torino", "80700");
        customer1 = new Customer("Mario", "Rossi", location1, "mario.rossi@example.com", "1234567890");
        customer2 = new Customer("Luca", "Verdi", location2, "luca.verdi@example.com", "1234567890");

        em.getTransaction().begin();

        customerDAO.insert(customer1);
        customerDAO.insert(customer2);

        recurringSchedule1 = new RecurringSchedule(customer1, wasteType, date,
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule1.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule1.setNextCollectionDate(date.plusDays(1));

        recurringSchedule2 = new RecurringSchedule(customer1, wasteType, date.plusDays(1),
                RecurringSchedule.Frequency.MONTHLY);
        recurringSchedule2.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule2.setNextCollectionDate(date.minusDays(1));

        recurringSchedule3 = new RecurringSchedule(customer1, wasteType, date.plusDays(2),
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule3.setScheduleStatus(RecurringSchedule.ScheduleStatus.CANCELLED);
        recurringSchedule3.setNextCollectionDate(date.minusDays(2));

        recurringSchedule4 = new RecurringSchedule(customer2, wasteType, date.plusDays(3),
                RecurringSchedule.Frequency.MONTHLY);
        recurringSchedule4.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule4.setNextCollectionDate(date.minusDays(5));

        recurringScheduleDAO.insert(recurringSchedule1);
        recurringScheduleDAO.insert(recurringSchedule2);
        recurringScheduleDAO.insert(recurringSchedule3);
        recurringScheduleDAO.insert(recurringSchedule4);

    }

    @Test
    void testFindActiveSchedulesWithoutFutureCollections() {
        LocalDate now = dateUtils.getCurrentDate();

        recurringSchedule1.setNextCollectionDate(now.plusDays(2));
        recurringScheduleDAO.update(recurringSchedule1);
        Collection c1 = new Collection(recurringSchedule1);
        c1.setCollectionDate(now.plusDays(2));
        collectionDAO.insert(c1);

        recurringSchedule2.setNextCollectionDate(now.plusDays(1));
        Collection c2 = new Collection(recurringSchedule2);
        c2.setCollectionDate(now);
        collectionDAO.insert(c2);

        recurringSchedule4.setNextCollectionDate(now.plusDays(3));
        recurringScheduleDAO.update(recurringSchedule4);

        List<RecurringSchedule> result = recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertFalse(result.contains(recurringSchedule1)); // EXCLUDED: has a future collection
        assertTrue(result.contains(recurringSchedule2)); // INCLUDED: has only a past collection
        assertFalse(result.contains(recurringSchedule3)); // EXCLUDED: is cancelled
        assertTrue(result.contains(recurringSchedule4)); // INCLUDED: no future collections
    }

    @Test
    void testFindActiveSchedulesWithNextDateBeforeToday() {
        List<RecurringSchedule> result = recurringScheduleDAO.findActiveSchedulesWithNextDateBeforeToday();

        assertNotNull(result);

        assertEquals(2, result.size());

        assertTrue(result.contains(recurringSchedule2));
        assertTrue(result.contains(recurringSchedule4));
        assertFalse(result.contains(recurringSchedule1));
        assertFalse(result.contains(recurringSchedule3));
    }

    @Test
    void testFindScheduleByCustomer() {
        List<RecurringSchedule> result1 = recurringScheduleDAO.findSchedulesByCustomer(customer1);
        List<RecurringSchedule> result2 = recurringScheduleDAO.findSchedulesByCustomer(customer2);

        assertNotNull(result1);
        assertEquals(3, result1.size());
        for (RecurringSchedule schedule : result1) {
            assertEquals(customer1, schedule.getCustomer());
        }

        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals(customer2, result2.get(0).getCustomer());
    }

    @Test
    void testNoResult() {
        recurringSchedule1.setScheduleStatus(RecurringSchedule.ScheduleStatus.CANCELLED);
        recurringSchedule2.setScheduleStatus(RecurringSchedule.ScheduleStatus.CANCELLED);
        recurringSchedule4.setScheduleStatus(RecurringSchedule.ScheduleStatus.CANCELLED);

        recurringScheduleDAO.update(recurringSchedule1);
        recurringScheduleDAO.update(recurringSchedule2);
        recurringScheduleDAO.update(recurringSchedule4);

        List<RecurringSchedule> result = recurringScheduleDAO.findActiveSchedulesWithoutFutureCollections();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullCustomer() {
        List<RecurringSchedule> result = recurringScheduleDAO.findSchedulesByCustomer(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
