package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for CollectionDAO.
 */
class CollectionDAOTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste waste;
    private Collection.CollectionStatus pending;
    private Collection.CollectionStatus inProgress;
    private Collection.CollectionStatus completed;
    private Collection.CollectionStatus cancelled;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;

    /**
     * Sets up test data before each test.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();
        date = LocalDate.now();
        waste = new Waste("PLASTIC", true, false);
        getWasteDAO().insert(waste);
        getWasteScheduleManager().setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        pending = Collection.CollectionStatus.PENDING;
        inProgress = Collection.CollectionStatus.IN_PROGRESS;
        completed = Collection.CollectionStatus.COMPLETED;
        cancelled = Collection.CollectionStatus.CANCELLED;

        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890");
        oneTimeSchedule = new OneTimeSchedule(customer, waste, date);
        recurringSchedule =
                new RecurringSchedule(customer, waste, date, Frequency.WEEKLY);
        getCustomerDAO().insert(customer);

        getOneTimeScheduleDAO().insert(oneTimeSchedule);
        getRecurringScheduleDAO().insert(recurringSchedule);
    }

    /**
     * Verifies collections are correctly filtered by status.
     */
    @Test
    void testFindCollectionByStatus() {
        Collection c1 = new Collection(oneTimeSchedule);
        getCollectionDAO().insert(c1);

        Collection c2 = new Collection(oneTimeSchedule);
        c2.setCollectionStatus(inProgress);
        getCollectionDAO().insert(c2);

        Collection c3 = new Collection(oneTimeSchedule);
        c3.setCollectionStatus(completed);
        getCollectionDAO().insert(c3);

        Collection c4 = new Collection(oneTimeSchedule);
        c4.setCollectionStatus(cancelled);
        getCollectionDAO().insert(c4);

        Collection c5 = new Collection(recurringSchedule);
        c5.setCollectionDate(LocalDate.now());
        getCollectionDAO().insert(c5);

        Collection c6 = new Collection(recurringSchedule);
        c6.setCollectionStatus(inProgress);
        c6.setCollectionDate(LocalDate.now());
        getCollectionDAO().insert(c6);

        Collection c7 = new Collection(recurringSchedule);
        c7.setCollectionStatus(completed);
        c7.setCollectionDate(LocalDate.now());
        getCollectionDAO().insert(c7);

        Collection c8 = new Collection(recurringSchedule);
        c8.setCollectionStatus(cancelled);
        c8.setCollectionDate(LocalDate.now());
        getCollectionDAO().insert(c8);

        assertEquals(2, getCollectionDAO().findCollectionByStatus(pending).size());
        assertEquals(2, getCollectionDAO().findCollectionByStatus(inProgress).size());
        assertEquals(2, getCollectionDAO().findCollectionByStatus(completed).size());
        assertEquals(2, getCollectionDAO().findCollectionByStatus(cancelled).size());
    }

    /**
     * Tests filtering collections by OneTimeSchedule.
     */
    @Test
    void testFindAllCollectionByOneTimeSchedule() {
        LocalDate newDate = LocalDate.now().plusDays(3);
        OneTimeSchedule schedule =
                getOneTimeScheduleManager().createOneTimeSchedule(customer, waste,
                        newDate);

        List<Collection> results =
                getCollectionDAO().findAllCollectionsBySchedule(schedule);
        Collection active = results.stream().filter(c -> c.getCollectionStatus()
                != Collection.CollectionStatus.CANCELLED).findFirst().orElse(null);

        assertNotNull(active);
        assertEquals(Collection.CollectionStatus.PENDING, active.getCollectionStatus());

        active.setCollectionStatus(cancelled);

        results = getCollectionDAO().findAllCollectionsBySchedule(schedule);
        active = results.stream().filter(c -> c.getCollectionStatus()
                != Collection.CollectionStatus.CANCELLED).findFirst().orElse(null);

        assertNull(active);
    }

    /**
     * Tests filtering collections by RecurringSchedule.
     */
    @Test
    void testFindAllCollectionByRecurringSchedule() {
        LocalDate newDate = LocalDate.now().plusDays(3);

        RecurringSchedule schedule =
                getRecurringScheduleManager().createRecurringSchedule(customer, waste,
                        newDate, Frequency.WEEKLY);

        getRecurringScheduleManager().updateStatusRecurringSchedule(schedule,
                RecurringSchedule.ScheduleStatus.PAUSED);
        getRecurringScheduleManager().updateStatusRecurringSchedule(schedule,
                RecurringSchedule.ScheduleStatus.ACTIVE);

        List<Collection> collections =
                getCollectionDAO().findAllCollectionsBySchedule(schedule);
        assertEquals(2, collections.size());

        long cancelledCount = collections.stream().filter(c -> c.getCollectionStatus()
                == Collection.CollectionStatus.CANCELLED).count();
        long activeCount = collections.stream().filter(c -> c.getCollectionStatus()
                != Collection.CollectionStatus.CANCELLED).count();

        assertEquals(1, cancelledCount);
        assertEquals(1, activeCount);
        assertTrue(collections.stream().allMatch(c -> c.getSchedule().equals(schedule)));
    }

    /**
     * Tests finding active collection by recurring schedule.
     */
    @Test
    void testFindActiveCollectionByRecurringSchedule() {
        LocalDate newDate = LocalDate.now().plusDays(4);
        recurringSchedule.setNextCollectionDate(newDate);
        recurringSchedule.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
        getRecurringScheduleDAO().update(recurringSchedule);

        Collection collection = new Collection(recurringSchedule);
        collection.setCollectionDate(newDate);
        collection.setCollectionStatus(Collection.CollectionStatus.PENDING);
        getCollectionDAO().insert(collection);

        Collection result = getCollectionDAO().findActiveCollectionByRecurringSchedule(
                recurringSchedule);
        assertNotNull(result);
        assertEquals(Collection.CollectionStatus.PENDING, result.getCollectionStatus());

        result.setCollectionStatus(Collection.CollectionStatus.CANCELLED);
        getCollectionDAO().update(result);

        Collection nullResult =
                getCollectionDAO().findActiveCollectionByRecurringSchedule(
                        recurringSchedule);
        assertNull(nullResult);
    }
}

