package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Waste;


public class CollectionDAOTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste.WasteType wasteType;
    private Collection.CollectionStatus pending;
    private Collection.CollectionStatus inProgress;
    private Collection.CollectionStatus completed;
    private Collection.CollectionStatus cancelled;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        date = dateUtils.getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;

        pending = Collection.CollectionStatus.PENDING;
        inProgress = Collection.CollectionStatus.IN_PROGRESS;
        completed = Collection.CollectionStatus.COMPLETED;
        cancelled = Collection.CollectionStatus.CANCELLED;

        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        oneTimeSchedule = new OneTimeSchedule(customer, wasteType, date);
        recurringSchedule = new RecurringSchedule(customer, wasteType, date, RecurringSchedule.Frequency.WEEKLY);
        customerDAO.insert(customer);
        oneTimeScheduleDAO.insert(oneTimeSchedule);
        recurringScheduleDAO.insert(recurringSchedule);
    }

    @Test
    public void testFindCollectionByStatus() {

        Collection c1 = new Collection(oneTimeSchedule);
        collectionDAO.insert(c1);
    
        Collection c2 = new Collection(oneTimeSchedule);
        c2.setCollectionStatus(inProgress);
        collectionDAO.insert(c2);
    
        Collection c3 = new Collection(oneTimeSchedule);
        c3.setCollectionStatus(completed);
        collectionDAO.insert(c3);
    
        Collection c4 = new Collection(oneTimeSchedule);
        c4.setCollectionStatus(cancelled);
        collectionDAO.insert(c4);
    
        Collection c5 = new Collection(recurringSchedule);
        c5.setCollectionDate(dateUtils.getCurrentDate());
        collectionDAO.insert(c5);
    
        Collection c6 = new Collection(recurringSchedule);
        c6.setCollectionStatus(inProgress);
        c6.setCollectionDate(dateUtils.getCurrentDate());
        collectionDAO.insert(c6);
    
        Collection c7 = new Collection(recurringSchedule);
        c7.setCollectionStatus(completed);
        c7.setCollectionDate(dateUtils.getCurrentDate());
        collectionDAO.insert(c7);
    
        Collection c8 = new Collection(recurringSchedule);        
        c8.setCollectionStatus(cancelled);
        c8.setCollectionDate(dateUtils.getCurrentDate());
        collectionDAO.insert(c8);
    
        assertEquals(2, collectionDAO.findCollectionByStatus(pending).size());
        assertEquals(2, collectionDAO.findCollectionByStatus(inProgress).size());
        assertEquals(2, collectionDAO.findCollectionByStatus(completed).size());
        assertEquals(2, collectionDAO.findCollectionByStatus(cancelled).size());
    }
}
