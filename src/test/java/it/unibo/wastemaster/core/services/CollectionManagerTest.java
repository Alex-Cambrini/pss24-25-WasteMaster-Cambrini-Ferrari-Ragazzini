package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule;
import it.unibo.wastemaster.core.models.Waste;

public class CollectionManagerTest extends AbstractDatabaseTest {
    private Customer customer;
    private Waste plastic;
    private Collection collection;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Location location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        LocalDate futureDate = LocalDate.now().plusDays(3);
        plastic = new Waste("PLASTIC", true, false);

        em.getTransaction().begin();
        wasteDAO.insert(plastic);
        customerDAO.insert(customer);

        oneTimeSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        oneTimeSchedule.setScheduleStatus(Schedule.ScheduleStatus.PAUSED);

        oneTimeScheduleDAO.insert(oneTimeSchedule);

        collection = new Collection(oneTimeSchedule);
        collectionDAO.insert(collection);

        recurringSchedule = new RecurringSchedule(customer, plastic, futureDate,
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        recurringSchedule.setNextCollectionDate(futureDate);
    }

    @Test
    public void testGetCollectionsByStatus() {
        List<Collection> pending = collectionManager.getCollectionsByStatus(CollectionStatus.PENDING);
        assertEquals(1, pending.size());
        assertEquals(CollectionStatus.PENDING, pending.get(0).getCollectionStatus());
    }

    @Test
    public void testGenerateCollection() {

        LocalDate futureDate = LocalDate.now().plusDays(5);
        OneTimeSchedule futureSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        futureSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        oneTimeScheduleDAO.insert(futureSchedule);

        collectionManager.generateCollection(futureSchedule);

        LocalDate pastDate = LocalDate.now().minusDays(2);
        OneTimeSchedule pastSchedule = new OneTimeSchedule(customer, plastic, pastDate);
        pastSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        collectionManager.generateCollection(pastSchedule);
        List<Collection> all = collectionDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testGenerateOneTimeCollection() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        OneTimeSchedule futureSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        futureSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        oneTimeScheduleDAO.insert(futureSchedule);

        collectionManager.generateOneTimeCollection(futureSchedule);

        List<Collection> all = collectionDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testGenerateRecurringCollections() {
        recurringScheduleDAO.insert(recurringSchedule);

        assertEquals(1, recurringScheduleManager.getRecurringSchedulesWithoutCollections().size());

        collectionManager.generateRecurringCollections();

        List<Collection> collections = collectionDAO.findAll();
        assertEquals(2, collections.size());
        assertEquals(plastic, collections.get(1).getSchedule().getWaste());
    }

    @Test
    public void testUpdateCollection() {
        Collection collection = collectionDAO.findAll().get(0);
        assertEquals(CollectionStatus.PENDING, collection.getCollectionStatus());

        collection.setCollectionStatus(CollectionStatus.COMPLETED);

        collectionManager.updateCollection(collection);

        Collection updated = collectionDAO.findById(collection.getCollectionId());
        assertEquals(CollectionStatus.COMPLETED, updated.getCollectionStatus());
    }

    @Test
    public void testGetAllCollectionBySchedule() {
        LocalDate date = LocalDate.now().plusDays(3);
        OneTimeSchedule schedule = oneTimeScheduleManager.createOneTimeSchedule(customer, plastic, date);

        Collection active = collectionManager.getAllCollectionBySchedule(schedule).get(0);
        assertNotNull(active);
        assertEquals(CollectionStatus.PENDING, active.getCollectionStatus());
        assertEquals(schedule, active.getSchedule());

        active.setCollectionStatus(CollectionStatus.CANCELLED);
        collectionManager.updateCollection(active);
        Collection deleted = collectionManager.getAllCollectionBySchedule(schedule).get(0);
        assertEquals(CollectionStatus.CANCELLED, deleted.getCollectionStatus());
    }

    @Test
    public void testGetActiveCollectionByRecurringSchedule() {
        LocalDate date = LocalDate.now().plusDays(5);

        RecurringSchedule schedule = new RecurringSchedule(customer, plastic, date, RecurringSchedule.Frequency.WEEKLY);
        schedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);
        schedule.setNextCollectionDate(date);

        recurringScheduleDAO.insert(schedule);
        collectionManager.generateCollection(schedule);

        Collection active = collectionManager.getActiveCollectionByRecurringSchedule(schedule);
        assertNotNull(active);
        assertEquals(CollectionStatus.PENDING, active.getCollectionStatus());
        assertEquals(schedule, active.getSchedule());

        active.setCollectionStatus(CollectionStatus.CANCELLED);
        collectionManager.updateCollection(active);

        Collection none = collectionManager.getActiveCollectionByRecurringSchedule(schedule);
        assertNull(none);
    }

    @Test
    public void testSoftDeleteCollection() {

        assertEquals(CollectionStatus.PENDING, collection.getCollectionStatus());
        boolean deleted = collectionManager.softDeleteCollection(collection);
        assertTrue(deleted);

        Collection updated = collectionDAO.findById(collection.getCollectionId());
        assertEquals(CollectionStatus.CANCELLED, updated.getCollectionStatus());

        boolean deletedAgain = collectionManager.softDeleteCollection(updated);
        assertTrue(!deletedAgain);
    }

}
