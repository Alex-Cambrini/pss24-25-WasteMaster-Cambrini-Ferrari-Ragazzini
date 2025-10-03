package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.Schedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectionManagerTest extends AbstractDatabaseTest {

    private static final int FUTURE_DAYS_FOR_POSTAL_CODE = 5;

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
        List<Collection> pending = getCollectionManager()
                .getCollectionsByStatus(CollectionStatus.ACTIVE);
        assertEquals(1, pending.size());
        assertEquals(CollectionStatus.ACTIVE, pending.get(0).getCollectionStatus());
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
        assertEquals(CollectionStatus.ACTIVE, collection.getCollectionStatus());

        collection.setCollectionStatus(CollectionStatus.COMPLETED);
        getCollectionManager().updateCollection(collection);

        Optional<Collection> updatedOpt =
                getCollectionDAO().findById(collection.getCollectionId());
        assertTrue(updatedOpt.isPresent());
        Collection updated = updatedOpt.get();
        assertEquals(CollectionStatus.COMPLETED, updated.getCollectionStatus());
    }

    @Test
    void testGetAllCollectionBySchedule() {
        OneTimeSchedule schedule = getOneTimeScheduleManager()
                .createOneTimeSchedule(customer, plastic, futureDate);

        Collection active =
                getCollectionManager().getAllCollectionBySchedule(schedule).get(0);
        assertNotNull(active);
        assertEquals(CollectionStatus.ACTIVE, active.getCollectionStatus());
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

        Optional<Collection> active =
                getCollectionManager().getActiveCollectionByRecurringSchedule(schedule);
        assertTrue(active.isPresent());
        assertEquals(CollectionStatus.ACTIVE, active.get().getCollectionStatus());
        assertEquals(schedule, active.get().getSchedule());

        active.get().setCollectionStatus(CollectionStatus.CANCELLED);
        getCollectionManager().updateCollection(active.get());

        Optional<Collection> none =
                getCollectionManager().getActiveCollectionByRecurringSchedule(schedule);
        assertFalse(none.isPresent());
    }

    @Test
    void testSoftDeleteCollection() {
        assertEquals(CollectionStatus.ACTIVE, collection.getCollectionStatus());
        boolean deleted = getCollectionManager().softDeleteCollection(collection);
        assertTrue(deleted);

        Optional<Collection> updated =
                getCollectionDAO().findById(collection.getCollectionId());
        assertTrue(updated.isPresent());
        assertEquals(CollectionStatus.CANCELLED, updated.get().getCollectionStatus());

        boolean deletedAgain = getCollectionManager().softDeleteCollection(updated.get());
        assertFalse(deletedAgain);
    }

    @Test
    void testGetCollectionsByPostalCode() {
        Location otherLocation = new Location("Via Milano", "5", "Bologna", "40121");
        Customer otherCustomer = new Customer("Luigi", "Bianchi", otherLocation,
                "luigi.bianchi@example.com",
                "0987654321");
        getCustomerDAO().insert(otherCustomer);

        LocalDate targetDate = LocalDate.now().plusDays(FUTURE_DAYS_FOR_POSTAL_CODE);

        OneTimeSchedule s1 = new OneTimeSchedule(customer, plastic, targetDate);
        s1.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s1);
        getCollectionDAO().insert(new Collection(s1));

        OneTimeSchedule s2 = new OneTimeSchedule(otherCustomer, plastic, targetDate);
        s2.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        getOneTimeScheduleDAO().insert(s2);
        getCollectionDAO().insert(new Collection(s2));
        List<Collection> result40100 =
                getCollectionManager().getCollectionsByPostalCode("40100", targetDate);

        assertEquals(1, result40100.size());
        assertEquals("40100",
                result40100.get(0).getCustomer().getLocation().getPostalCode());
        assertEquals(targetDate, result40100.get(0).getCollectionDate());

        List<Collection> result40121 =
                getCollectionManager().getCollectionsByPostalCode("40121", targetDate);

        assertEquals(1, result40121.size());
        assertEquals("40121",
                result40121.get(0).getCustomer().getLocation().getPostalCode());
        assertEquals(targetDate, result40121.get(0).getCollectionDate());
    }

}
