package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for RecurringScheduleDAO.
 */
class RecurringScheduleDAOTest extends AbstractDatabaseTest {

    private Location location1;
    private Location location2;
    private Customer customer1;
    private Customer customer2;
    private Waste waste;
    private RecurringSchedule recurringSchedule1;
    private RecurringSchedule recurringSchedule2;
    private RecurringSchedule recurringSchedule3;
    private RecurringSchedule recurringSchedule4;
    private LocalDate date;

    /**
     * Initializes test data before each test.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        final int fiveDaysAgo = 5;
        date = LocalDate.now();
        waste = new Waste("PLASTICA", true, false);

        location1 = new Location("Via Roma", "10", "Bologna", "40100");
        location2 = new Location("Via Milano", "32", "Torino", "80700");
        customer1 = new Customer("Mario", "Rossi", location1, "mario.rossi@example.com",
                "1234567890");
        customer2 = new Customer("Luca", "Verdi", location2, "luca.verdi@example.com",
                "1234567890");

        getWasteDAO().insert(waste);
        getCustomerDAO().insert(customer1);
        getCustomerDAO().insert(customer2);

        recurringSchedule1 = new RecurringSchedule(customer1, waste, date,
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule1.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule1.setNextCollectionDate(date.plusDays(1));

        recurringSchedule2 = new RecurringSchedule(customer1, waste, date.plusDays(1),
                RecurringSchedule.Frequency.MONTHLY);
        recurringSchedule2.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule2.setNextCollectionDate(date.minusDays(1));

        recurringSchedule3 = new RecurringSchedule(customer1, waste, date.plusDays(2),
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule3.setScheduleStatus(RecurringSchedule.ScheduleStatus.CANCELLED);
        recurringSchedule3.setNextCollectionDate(date.minusDays(2));

        recurringSchedule4 = new RecurringSchedule(customer2, waste, date.plusDays(3),
                RecurringSchedule.Frequency.MONTHLY);
        recurringSchedule4.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        recurringSchedule4.setNextCollectionDate(date.minusDays(fiveDaysAgo));

        getRecurringScheduleDAO().insert(recurringSchedule1);
        getRecurringScheduleDAO().insert(recurringSchedule2);
        getRecurringScheduleDAO().insert(recurringSchedule3);
        getRecurringScheduleDAO().insert(recurringSchedule4);
    }

    @Test
    void testFindActiveSchedulesWithoutFutureCollections() {
        LocalDate now = LocalDate.now();

        recurringSchedule1.setNextCollectionDate(now.plusDays(2));
        getRecurringScheduleDAO().update(recurringSchedule1);
        Collection c1 = new Collection(recurringSchedule1);
        c1.setCollectionDate(now.plusDays(2));
        getCollectionDAO().insert(c1);

        recurringSchedule2.setNextCollectionDate(now.plusDays(1));
        Collection c2 = new Collection(recurringSchedule2);
        c2.setCollectionDate(now);
        getCollectionDAO().insert(c2);

        recurringSchedule4.setNextCollectionDate(now.plusDays(3));
        getRecurringScheduleDAO().update(recurringSchedule4);

        List<RecurringSchedule> result =
                getRecurringScheduleDAO().findActiveSchedulesWithoutFutureCollections();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertFalse(result.contains(recurringSchedule1));
        assertTrue(result.contains(recurringSchedule2));
        assertFalse(result.contains(recurringSchedule3));
        assertTrue(result.contains(recurringSchedule4));
    }

    @Test
    void testFindActiveSchedulesWithNextDateBeforeToday() {
        List<RecurringSchedule> result =
                getRecurringScheduleDAO().findActiveSchedulesWithNextDateBeforeToday();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.contains(recurringSchedule2));
        assertTrue(result.contains(recurringSchedule4));
        assertFalse(result.contains(recurringSchedule1));
        assertFalse(result.contains(recurringSchedule3));
    }

    @Test
    void testFindScheduleByCustomer() {
        List<RecurringSchedule> result1 =
                getRecurringScheduleDAO().findSchedulesByCustomer(customer1);
        List<RecurringSchedule> result2 =
                getRecurringScheduleDAO().findSchedulesByCustomer(customer2);

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

        getRecurringScheduleDAO().update(recurringSchedule1);
        getRecurringScheduleDAO().update(recurringSchedule2);
        getRecurringScheduleDAO().update(recurringSchedule4);

        List<RecurringSchedule> result =
                getRecurringScheduleDAO().findActiveSchedulesWithoutFutureCollections();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullCustomer() {
        List<RecurringSchedule> result =
                getRecurringScheduleDAO().findSchedulesByCustomer(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
