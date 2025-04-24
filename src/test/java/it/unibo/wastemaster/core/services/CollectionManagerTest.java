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
        locationDAO.insert(location);
        customerDAO.insert(customer);
        em.getTransaction().commit();

        oneTimeSchedule = new OneTimeSchedule(customer, Waste.WasteType.PLASTIC, futureDate);
        oneTimeSchedule.setStatus(Schedule.ScheduleStatus.SCHEDULED);

        em.getTransaction().begin();
        oneTimeScheduleDAO.insert(oneTimeSchedule);
        em.getTransaction().commit();

        Collection collection = new Collection(oneTimeSchedule);
        em.getTransaction().begin();
        collectionDAO.insert(collection);
        em.getTransaction().commit();

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

}
