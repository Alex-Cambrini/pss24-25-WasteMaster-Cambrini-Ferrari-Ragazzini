package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.models.Waste;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class CollectionManagerTest extends AbstractDatabaseTest {
    private Customer customer;
    private Waste plastic;
    private Collection collection;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;
    private LocalDate futureDate;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        Location location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890");
        futureDate = LocalDate.now().plusDays(3);
        plastic = new Waste("PLASTIC", true, false);

        getEntityManager().getTransaction().begin();
        getWasteDAO().insert(plastic);
        getCustomerDAO().insert(customer);

        oneTimeSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        oneTimeSchedule.setScheduleStatus(Schedule.ScheduleStatus.PAUSED);

        getOneTimeScheduleDAO().insert(oneTimeSchedule);

        collection = new Collection(oneTimeSchedule);
        getCollectionDAO().insert(collection);

        recurringSchedule = new RecurringSchedule(customer, plastic, futureDate,
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        recurringSchedule.setNextCollectionDate(futureDate);
    }

    @Test
    void testGetCollectionsByStatus() {
        List<Collection> pending =
                getCollectionManager().getCollectionsByStatus(CollectionStatus.PENDING);
        assertEquals(1, pending.size());
        assertEquals(CollectionStatus.PENDING, pending.get(0).getCollectionStatus());
    }

    @Test
    void testGenerateCollection() {
        OneTimeSchedule futureSchedule =
                new OneTimeSchedule(customer, plastic, futureDate);
        futureSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        getOneTimeScheduleDAO().insert(futureSchedule);

        getCollectionManager().generateCollection(futureSchedule);

        LocalDate pastDate = LocalDate.now().minusDays(2);
        OneTimeSchedule pastSchedule = new OneTimeSchedule(customer, plastic, pastDate);
        pastSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        getCollectionManager().generateCollection(pastSchedule);
        List<Collection> all = getCollectionDAO().findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testGenerateOneTimeCollection() {
        OneTimeSchedule futureSchedule =
                new OneTimeSchedule(customer, plastic, futureDate);
        futureSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        getOneTimeScheduleDAO().insert(futureSchedule);

        getCollectionManager().generateOneTimeCollection(futureSchedule);

        List<Collection> all = getCollectionDAO().findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testGenerateRecurringCollections() {
        getRecurringScheduleDAO().insert(recurringSchedule);

        assertEquals(1, getRecurringScheduleManager()
                .getRecurringSchedulesWithoutCollections().size());

        getCollectionManager().generateRecurringCollections();

        List<Collection> collections = getCollectionDAO().findAll();
        assertEquals(2, collections.size());
        assertEquals(plastic, collections.get(1).getSchedule().getWaste());
    }

    @Test
    void testUpdateCollection() {
        assertEquals(CollectionStatus.PENDING, collection.getCollectionStatus());

        collection.setCollectionStatus(CollectionStatus.COMPLETED);
        getCollectionManager().updateCollection(collection);

        Collection updated = getCollectionDAO().findById(collection.getCollectionId());
        assertEquals(CollectionStatus.COMPLETED, updated.getCollectionStatus());
    }

    @Test
    void testGetAllCollectionBySchedule() {
        OneTimeSchedule schedule = getOneTimeScheduleManager()
                .createOneTimeSchedule(customer, plastic, futureDate);

        Collection active =
                getCollectionManager().getAllCollectionBySchedule(schedule).get(0);
        assertNotNull(active);
        assertEquals(CollectionStatus.PENDING, active.getCollectionStatus());
        assertEquals(schedule, active.getSchedule());

        active.setCollectionStatus(CollectionStatus.CANCELLED);
        getCollectionManager().updateCollection(active);
        Collection deleted =
                getCollectionManager().getAllCollectionBySchedule(schedule).get(0);
        assertEquals(CollectionStatus.CANCELLED, deleted.getCollectionStatus());
    }

    @Test
    void testGetActiveCollectionByRecurringSchedule() {
        RecurringSchedule schedule = new RecurringSchedule(customer, plastic, futureDate,
                RecurringSchedule.Frequency.WEEKLY);
        schedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        schedule.setNextCollectionDate(futureDate);

        getRecurringScheduleDAO().insert(schedule);
        getCollectionManager().generateCollection(schedule);

        Collection active =
                getCollectionManager().getActiveCollectionByRecurringSchedule(schedule);
        assertNotNull(active);
        assertEquals(CollectionStatus.PENDING, active.getCollectionStatus());
        assertEquals(schedule, active.getSchedule());

        active.setCollectionStatus(CollectionStatus.CANCELLED);
        getCollectionManager().updateCollection(active);

        Collection none =
                getCollectionManager().getActiveCollectionByRecurringSchedule(schedule);
        assertNull(none);
    }

    @Test
    void testSoftDeleteCollection() {

        assertEquals(CollectionStatus.PENDING, collection.getCollectionStatus());
        boolean deleted = getCollectionManager().softDeleteCollection(collection);
        assertTrue(deleted);

        Collection updated = getCollectionDAO().findById(collection.getCollectionId());
        assertEquals(CollectionStatus.CANCELLED, updated.getCollectionStatus());

        boolean deletedAgain = getCollectionManager().softDeleteCollection(updated);
        assertTrue(!deletedAgain);
    }
}
