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
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Location location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        LocalDate futureDate = dateUtils.getCurrentDate().plusDays(3);
        plastic = new Waste("PLASTICA", true, false);
        

        em.getTransaction().begin();
        wasteDAO.insert(plastic);
        customerDAO.insert(customer);

        oneTimeSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        oneTimeSchedule.setScheduleStatus(Schedule.ScheduleStatus.PAUSED);

        oneTimeScheduleDAO.insert(oneTimeSchedule);

        Collection collection = new Collection(oneTimeSchedule);
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

        LocalDate futureDate = dateUtils.getCurrentDate().plusDays(5);
        OneTimeSchedule futureSchedule = new OneTimeSchedule(customer, plastic, futureDate);
        futureSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        oneTimeScheduleDAO.insert(futureSchedule);

        collectionManager.generateCollection(futureSchedule);

        LocalDate pastDate = dateUtils.getCurrentDate().minusDays(2);
        OneTimeSchedule pastSchedule = new OneTimeSchedule(customer, plastic, pastDate);
        pastSchedule.setScheduleStatus(Schedule.ScheduleStatus.ACTIVE);

        collectionManager.generateCollection(pastSchedule);
        List<Collection> all = collectionDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testGenerateOneTimeCollection() {
        LocalDate futureDate = dateUtils.getCurrentDate().plusDays(7);
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
    public void testGetActiveCollectionByOneTimeSchedule() {
        LocalDate date = dateUtils.getCurrentDate().plusDays(3);
        OneTimeSchedule schedule = oneTimeScheduleManager.createOneTimeSchedule(customer, plastic, date);

        Collection active = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
        assertNotNull(active);
        assertEquals(CollectionStatus.PENDING, active.getCollectionStatus());
        assertEquals(schedule, active.getSchedule());

        active.setCollectionStatus(CollectionStatus.CANCELLED);
        collectionManager.updateCollection(active);
        Collection none = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
        assertNull(none);
    }

@Test
public void testGetCancelledCollectionsOneTimeSchedule() {
    LocalDate date = dateUtils.getCurrentDate().plusDays(3);
    OneTimeSchedule schedule = oneTimeScheduleManager.createOneTimeSchedule(customer, plastic, date);

    List<Collection> cancelledBefore = collectionManager.getCancelledCollectionsOneTimeSchedule(schedule);
    assertTrue(cancelledBefore.isEmpty());

    Collection c1 = collectionManager.getActiveCollectionByOneTimeSchedule(schedule);
    c1.setCollectionStatus(CollectionStatus.CANCELLED);
    collectionManager.updateCollection(c1);

    Collection c2 = new Collection(schedule);
    c2.setCollectionStatus(CollectionStatus.CANCELLED);
    collectionDAO.insert(c2);

    List<Collection> cancelled = collectionManager.getCancelledCollectionsOneTimeSchedule(schedule);
    assertEquals(2, cancelled.size());
    cancelled.forEach(c -> {
        assertEquals(CollectionStatus.CANCELLED, c.getCollectionStatus());
        assertEquals(schedule, c.getSchedule());
    });
}


}