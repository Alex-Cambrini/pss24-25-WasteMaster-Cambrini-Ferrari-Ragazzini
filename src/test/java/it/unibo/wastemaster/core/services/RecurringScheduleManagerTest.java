package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;

class RecurringScheduleManagerTest extends AbstractDatabaseTest {
    private Location location;
    private Customer customer;
    private Waste waste;
    private WasteSchedule wasteSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        waste = new Waste("glass", true, false);
        wasteDAO.insert(waste);
        wasteSchedule = new WasteSchedule(waste, DayOfWeek.MONDAY);
        customerDAO.insert(customer);
        wasteScheduleDAO.insert(wasteSchedule);
    }

    @Test
    void testCreateRecurringSchedule() {
        recurringScheduleManager.createRecurringSchedule(customer, waste, dateUtils.getCurrentDate(),
                Frequency.WEEKLY);
        recurringScheduleManager.createRecurringSchedule(customer, waste, dateUtils.getCurrentDate(),
                Frequency.MONTHLY);

        List<RecurringSchedule> schedules = recurringScheduleDAO.findSchedulesByCustomer(customer);
        assertEquals(2, schedules.size());

        RecurringSchedule s1 = schedules.get(0);
        assertEquals(waste, s1.getWaste());
        assertEquals(Frequency.WEEKLY, s1.getFrequency());
        assertNotNull(s1.getNextCollectionDate());

        RecurringSchedule s2 = schedules.get(1);
        assertEquals(waste, s2.getWaste());
        assertEquals(Frequency.MONTHLY, s2.getFrequency());
        assertNotNull(s2.getNextCollectionDate());

        LocalDate pastDate = dateUtils.getCurrentDate().minusDays(1);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            recurringScheduleManager.createRecurringSchedule(customer, waste, pastDate, Frequency.WEEKLY);
        });
        assertEquals("Start Date must be today or in the future", thrown.getMessage());
    }

    @Test
    void testCalculateNextDate_FirstCollection() {
        LocalDate startDate = LocalDate.of(2025, 4, 24);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste,
                startDate, Frequency.WEEKLY);

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_MonthlyCollection() {
        LocalDate startDate = LocalDate.of(2025, 4, 24);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.MONTHLY);

        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 28));

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 6, 2);
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_InThePast() {
        DateUtils mockDateUtils = new DateUtils() {
            @Override
            public LocalDate getCurrentDate() {
                return LocalDate.of(2025, 5, 1);
            }
        };

        recurringScheduleManager.setDateUtils(mockDateUtils);

        LocalDate startDate = LocalDate.of(2025, 4, 1);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);

        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 10));

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 5, 5);

        assertTrue(nextDate.isAfter(LocalDate.of(2025, 5, 1)));
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testAlignToScheduledDay() {
        LocalDate startDate = LocalDate.of(2025, 4, 25);
        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);
        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);

        schedule.setStartDate(LocalDate.of(2025, 4, 30));
        schedule.setNextCollectionDate(null);

        nextDate = recurringScheduleManager.calculateNextDate(schedule);
        expectedDate = LocalDate.of(2025, 5, 5);

        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testUpdateNextDates() {
        LocalDate today = dateUtils.getCurrentDate();
        LocalDate startDate = today;
        LocalDate oldNextDate = today.minusDays(2);

        wasteDAO.insert(waste);
        wasteScheduleDAO.insert(new WasteSchedule(waste, DayOfWeek.MONDAY));

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);
        schedule.setNextCollectionDate(oldNextDate);
        schedule.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);

        ValidateUtils.validateEntity(schedule);
        recurringScheduleDAO.insert(schedule);

        recurringScheduleManager.updateNextDates();

        RecurringSchedule updated = recurringScheduleDAO.findSchedulesByCustomer(customer).get(0);
        assertTrue(updated.getNextCollectionDate().isAfter(oldNextDate));
    }

    @Test
    void testUpdateStatusRecurringSchedule() {
        LocalDate validDate = dateUtils.getCurrentDate().plusDays(3);

        // Null arguments throw exception
        assertThrows(IllegalArgumentException.class,
                () -> recurringScheduleManager.updateStatusRecurringSchedule(null, ScheduleStatus.ACTIVE));
        RecurringSchedule s0 = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        assertThrows(IllegalArgumentException.class,
                () -> recurringScheduleManager.updateStatusRecurringSchedule(s0, null));

        // Cannot update CANCELLED schedule
        RecurringSchedule s1 = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s1.setScheduleStatus(ScheduleStatus.CANCELLED);
        assertFalse(recurringScheduleManager.updateStatusRecurringSchedule(s1, ScheduleStatus.ACTIVE));

        // Cannot update COMPLETED schedule
        RecurringSchedule sCompleted = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        sCompleted.setScheduleStatus(ScheduleStatus.COMPLETED);
        assertFalse(recurringScheduleManager.updateStatusRecurringSchedule(sCompleted, ScheduleStatus.ACTIVE));

        wasteScheduleDAO.insert(new WasteSchedule(waste, DayOfWeek.MONDAY));

        // ACTIVE -> PAUSED: update status and soft delete associated collection
        RecurringSchedule s2 = recurringScheduleManager.createRecurringSchedule(customer, waste, validDate,
                Frequency.WEEKLY);
        Collection associatedCollection = collectionManager.getActiveCollectionByRecurringSchedule(s2);
        int associatedCollectionId = associatedCollection.getCollectionId();

        assertNotEquals(CollectionStatus.CANCELLED, associatedCollection.getCollectionStatus());
        assertTrue(recurringScheduleManager.updateStatusRecurringSchedule(s2, ScheduleStatus.PAUSED));

        RecurringSchedule reloaded2 = recurringScheduleDAO.findById(s2.getScheduleId());
        associatedCollection = collectionDAO.findById(associatedCollectionId);

        assertEquals(ScheduleStatus.PAUSED, reloaded2.getScheduleStatus());
        assertEquals(CollectionStatus.CANCELLED, associatedCollection.getCollectionStatus());

        // PAUSED -> ACTIVE: recalc next date, update status, generate new collection
        assertTrue(recurringScheduleManager.updateStatusRecurringSchedule(reloaded2, ScheduleStatus.ACTIVE));

        RecurringSchedule reloaded3 = recurringScheduleDAO.findById(s2.getScheduleId());
        assertEquals(ScheduleStatus.ACTIVE, reloaded3.getScheduleStatus());
        associatedCollection = collectionManager.getActiveCollectionByRecurringSchedule(reloaded3);
        assertNotNull(associatedCollection);
        assertNotNull(reloaded3.getNextCollectionDate());

        // ACTIVE -> CANCELLED: update status and soft delete collection
        assertTrue(recurringScheduleManager.updateStatusRecurringSchedule(reloaded3, ScheduleStatus.CANCELLED));
        RecurringSchedule reloaded4 = recurringScheduleDAO.findById(s2.getScheduleId());
        associatedCollection = collectionManager.getActiveCollectionByRecurringSchedule(reloaded4);
        assertNull(associatedCollection);
        assertEquals(ScheduleStatus.CANCELLED, reloaded4.getScheduleStatus());

        // Cannot reactivate CANCELLED schedule
        assertFalse(recurringScheduleManager.updateStatusRecurringSchedule(reloaded4, ScheduleStatus.ACTIVE));
    }

    @Test
    void testUpdateFrequency() {
        LocalDate validDate = dateUtils.getCurrentDate().plusDays(3);

        // Null arguments throw exception
        assertThrows(IllegalArgumentException.class,
                () -> recurringScheduleManager.updateFrequency(null, Frequency.WEEKLY));
        RecurringSchedule s0 = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        assertThrows(IllegalArgumentException.class,
                () -> recurringScheduleManager.updateFrequency(s0, null));

        // Cannot update frequency if schedule is not ACTIVE
        RecurringSchedule s1 = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s1.setScheduleStatus(ScheduleStatus.PAUSED);
        assertFalse(recurringScheduleManager.updateFrequency(s1, Frequency.MONTHLY));

        // No update if new frequency equals current frequency
        RecurringSchedule s2 = new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s2.setScheduleStatus(ScheduleStatus.ACTIVE);
        assertFalse(recurringScheduleManager.updateFrequency(s2, Frequency.WEEKLY));

        // Update frequency successfully
        RecurringSchedule s3 = recurringScheduleManager.createRecurringSchedule(customer, waste, validDate,
                Frequency.WEEKLY);
        s3.setScheduleStatus(ScheduleStatus.ACTIVE);

        LocalDate oldNextDate = s3.getNextCollectionDate();

        boolean updated = recurringScheduleManager.updateFrequency(s3, Frequency.MONTHLY);
        assertTrue(updated);

        RecurringSchedule reloaded = recurringScheduleDAO.findById(s3.getScheduleId());
        assertEquals(Frequency.MONTHLY, reloaded.getFrequency());

        // Verifica che nextCollectionDate sia >= today+2 (non serve uguaglianza esatta)
        LocalDate todayPlus2 = dateUtils.getCurrentDate().plusDays(2);
        assertFalse(reloaded.getNextCollectionDate().isBefore(todayPlus2));

        // Verifica che la collection precedente sia stata cancellata e creata una nuova
        // attiva
        Collection activeCollection = collectionManager.getActiveCollectionByRecurringSchedule(reloaded);
        assertNotNull(activeCollection);
        assertNotEquals(CollectionStatus.CANCELLED, activeCollection.getCollectionStatus());
    }

    @Test
    void testGetSchedulesByCustomer() {
        RecurringSchedule schedule = new RecurringSchedule(customer, waste, dateUtils.getCurrentDate(),
                Frequency.WEEKLY);
        recurringScheduleDAO.insert(schedule);

        List<RecurringSchedule> result = recurringScheduleManager.getSchedulesByCustomer(customer);

        assertEquals(1, result.size());
        assertEquals(customer, result.get(0).getCustomer());
        assertEquals(waste, result.get(0).getWaste());
    }
}
