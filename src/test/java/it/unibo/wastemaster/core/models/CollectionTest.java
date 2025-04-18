package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.utils.DateUtils;

class CollectionTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private Date date;
    private Waste.WasteType wasteType;
    private Collection.CollectionStatus status;
    private Collection.ScheduleCategory scheduleCategory;
    private OneTimeSchedule schedule;
    private Collection collection;

    @BeforeEach

    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");

        date = DateUtils.getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;
        status = Collection.CollectionStatus.COMPLETED;
        scheduleCategory = Collection.ScheduleCategory.ONE_TIME;
        
        schedule = new OneTimeSchedule(customer, wasteType, ScheduleStatus.SCHEDULED,
                new java.sql.Date(System.currentTimeMillis()));

        collection = new Collection(customer, date, wasteType, status, schedule, scheduleCategory);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(customer, collection.getCustomer());
        assertEquals(date, collection.getDate());
        assertEquals(wasteType, collection.getWaste());
        assertEquals(status, collection.getCollectionStatus());
        assertEquals(scheduleCategory, collection.getScheduleCategory());
        assertEquals(Collection.CANCEL_LIMIT_DAYS, collection.getCancelLimitDays());
    }

    @Test
    void testToString() {
        String toStringOutput = collection.toString();
        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("Collection"));
        assertTrue(toStringOutput.contains(customer.getName()));
        assertTrue(toStringOutput.contains(wasteType.name()));
        assertTrue(toStringOutput.contains(status.name()));
        assertTrue(toStringOutput.contains(scheduleCategory.name()));
        assertTrue(toStringOutput.contains(String.valueOf(Collection.CANCEL_LIMIT_DAYS)));
    }

    @Test
    void testPersistence() {

        em.getTransaction().begin();
        em.persist(location);
        em.persist(customer);
        em.persist(schedule);
        em.persist(collection);
        em.getTransaction().commit();

        Collection found = em.find(Collection.class, collection.getCollectionId());
        assertNotNull(found);
        assertEquals(customer.getName(), found.getCustomer().getName());
    }
}
