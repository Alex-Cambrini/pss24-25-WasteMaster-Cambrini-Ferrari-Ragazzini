package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import it.unibo.wastemaster.core.utils.DateUtils;

public class CollectionManagerTest extends AbstractDatabaseTest {
    private Customer customer;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Location location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        LocalDate futureDate = DateUtils.getCurrentDate().plusDays(3);

        em.getTransaction().begin();
        customerDAO.insert(customer);

        oneTimeSchedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, futureDate);
        oneTimeSchedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);

        oneTimeScheduleDAO.insert(oneTimeSchedule);

        Collection collection = new Collection(oneTimeSchedule);
        collectionDAO.insert(collection);

        recurringSchedule = new RecurringSchedule(customer, Waste.WasteType.GLASS, futureDate,
                RecurringSchedule.Frequency.WEEKLY);
        recurringSchedule.setStatus(Schedule.ScheduleStatus.ACTIVE);
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

        LocalDate futureDate = DateUtils.getCurrentDate().plusDays(5);
        OneTimeSchedule futureSchedule = new OneTimeSchedule(customer, Waste.WasteType.PAPER, futureDate);
        futureSchedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);

        oneTimeScheduleDAO.insert(futureSchedule);

        collectionManager.generateCollection(futureSchedule);

        LocalDate pastDate = DateUtils.getCurrentDate().minusDays(2);
        OneTimeSchedule pastSchedule = new OneTimeSchedule(customer, Waste.WasteType.GLASS, pastDate);
        pastSchedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);

        collectionManager.generateCollection(pastSchedule);
        List<Collection> all = collectionDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testGenerateOneTimeCollection() {
        LocalDate futureDate = DateUtils.getCurrentDate().plusDays(7);
        OneTimeSchedule futureSchedule = new OneTimeSchedule(customer, Waste.WasteType.PAPER, futureDate);
        futureSchedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);

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
        assertEquals(Waste.WasteType.GLASS, collections.get(1).getSchedule().getWasteType());
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

}