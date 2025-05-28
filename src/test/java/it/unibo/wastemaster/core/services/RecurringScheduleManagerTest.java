package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Collection.CollectionStatus;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class RecurringScheduleManagerTest extends AbstractDatabaseTest {
    private Location location;
    private Customer customer;
    private Waste waste;
    private WasteSchedule wasteSchedule;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com",
                "1234567890");
        waste = new Waste("glass", true, false);
        getWasteDAO().insert(waste);
        wasteSchedule = new WasteSchedule(waste, DayOfWeek.MONDAY);
        getCustomerDAO().insert(customer);
        getWasteScheduleDAO().insert(wasteSchedule);
    }

    @Test
    void testCreateRecurringSchedule() {
        getRecurringScheduleManager().createRecurringSchedule(customer, waste,
                LocalDate.now(), Frequency.WEEKLY);
        getRecurringScheduleManager().createRecurringSchedule(customer, waste,
                LocalDate.now(), Frequency.MONTHLY);

        List<RecurringSchedule> schedules =
                getRecurringScheduleDAO().findSchedulesByCustomer(customer);
        assertEquals(2, schedules.size());

        RecurringSchedule s1 = schedules.get(0);
        assertEquals(waste, s1.getWaste());
        assertEquals(Frequency.WEEKLY, s1.getFrequency());
        assertNotNull(s1.getNextCollectionDate());

        RecurringSchedule s2 = schedules.get(1);
        assertEquals(waste, s2.getWaste());
        assertEquals(Frequency.MONTHLY, s2.getFrequency());
        assertNotNull(s2.getNextCollectionDate());

        LocalDate pastDate = LocalDate.now().minusDays(1);
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> {
                    getRecurringScheduleManager().createRecurringSchedule(customer, waste,
                            pastDate, Frequency.WEEKLY);
                });
        assertEquals("Start Date must be today or in the future", thrown.getMessage());
    }

    @Test
    void testUpdateNextDates() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today;
        LocalDate oldNextDate = today.minusDays(2);

        getWasteDAO().insert(waste);
        getWasteScheduleDAO().insert(new WasteSchedule(waste, DayOfWeek.MONDAY));

        RecurringSchedule schedule =
                new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);
        schedule.setNextCollectionDate(oldNextDate);
        schedule.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);

        ValidateUtils.validateEntity(schedule);
        getRecurringScheduleDAO().insert(schedule);

        getRecurringScheduleManager().updateNextDates();

        RecurringSchedule updated =
                getRecurringScheduleDAO().findSchedulesByCustomer(customer).get(0);
        assertTrue(updated.getNextCollectionDate().isAfter(oldNextDate));
    }

    @Test
    void testUpdateStatusRecurringSchedule() {
        LocalDate validDate = LocalDate.now().plusDays(3);

        // Null arguments throw exception
        assertThrows(IllegalArgumentException.class, () -> getRecurringScheduleManager()
                .updateStatusRecurringSchedule(null, ScheduleStatus.ACTIVE));
        RecurringSchedule s0 =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        assertThrows(IllegalArgumentException.class, () -> getRecurringScheduleManager()
                .updateStatusRecurringSchedule(s0, null));

        // Cannot update CANCELLED schedule
        RecurringSchedule s1 =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s1.setScheduleStatus(ScheduleStatus.CANCELLED);
        assertFalse(getRecurringScheduleManager().updateStatusRecurringSchedule(s1,
                ScheduleStatus.ACTIVE));

        // Cannot update COMPLETED schedule
        RecurringSchedule sCompleted =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        sCompleted.setScheduleStatus(ScheduleStatus.COMPLETED);
        assertFalse(getRecurringScheduleManager()
                .updateStatusRecurringSchedule(sCompleted, ScheduleStatus.ACTIVE));

        getWasteScheduleDAO().insert(new WasteSchedule(waste, DayOfWeek.MONDAY));

        // ACTIVE -> PAUSED: update status and soft delete associated collection
        RecurringSchedule s2 = getRecurringScheduleManager()
                .createRecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        Collection associatedCollection =
                getCollectionManager().getActiveCollectionByRecurringSchedule(s2);
        int associatedCollectionId = associatedCollection.getCollectionId();

        assertNotEquals(CollectionStatus.CANCELLED,
                associatedCollection.getCollectionStatus());
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(s2,
                ScheduleStatus.PAUSED));

        RecurringSchedule reloaded2 =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        associatedCollection = getCollectionDAO().findById(associatedCollectionId);

        assertEquals(ScheduleStatus.PAUSED, reloaded2.getScheduleStatus());
        assertEquals(CollectionStatus.CANCELLED,
                associatedCollection.getCollectionStatus());

        // PAUSED -> ACTIVE: recalc next date, update status, generate new collection
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(reloaded2,
                ScheduleStatus.ACTIVE));

        RecurringSchedule reloaded3 =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        assertEquals(ScheduleStatus.ACTIVE, reloaded3.getScheduleStatus());
        associatedCollection =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded3);
        assertNotNull(associatedCollection);
        assertNotNull(reloaded3.getNextCollectionDate());

        // ACTIVE -> CANCELLED: update status and soft delete collection
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(reloaded3,
                ScheduleStatus.CANCELLED));
        RecurringSchedule reloaded4 =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        associatedCollection =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded4);
        assertNull(associatedCollection);
        assertEquals(ScheduleStatus.CANCELLED, reloaded4.getScheduleStatus());

        // Cannot reactivate CANCELLED schedule
        assertFalse(getRecurringScheduleManager().updateStatusRecurringSchedule(reloaded4,
                ScheduleStatus.ACTIVE));
    }

    @Test
    void testUpdateFrequency() {
        LocalDate validDate = LocalDate.now().plusDays(3);

        // Null arguments throw exception
        assertThrows(IllegalArgumentException.class, () -> getRecurringScheduleManager()
                .updateFrequency(null, Frequency.WEEKLY));
        RecurringSchedule s0 =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        assertThrows(IllegalArgumentException.class,
                () -> getRecurringScheduleManager().updateFrequency(s0, null));

        RecurringSchedule s1 =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s1.setScheduleStatus(ScheduleStatus.PAUSED);
        assertFalse(getRecurringScheduleManager().updateFrequency(s1, Frequency.MONTHLY));

        RecurringSchedule s2 =
                new RecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s2.setScheduleStatus(ScheduleStatus.ACTIVE);
        assertFalse(getRecurringScheduleManager().updateFrequency(s2, Frequency.WEEKLY));

        RecurringSchedule s3 = getRecurringScheduleManager()
                .createRecurringSchedule(customer, waste, validDate, Frequency.WEEKLY);
        s3.setScheduleStatus(ScheduleStatus.ACTIVE);

        boolean updated =
                getRecurringScheduleManager().updateFrequency(s3, Frequency.MONTHLY);
        assertTrue(updated);

        RecurringSchedule reloaded =
                getRecurringScheduleDAO().findById(s3.getScheduleId());
        assertEquals(Frequency.MONTHLY, reloaded.getFrequency());


        LocalDate todayPlus2 = LocalDate.now().plusDays(2);
        assertFalse(reloaded.getNextCollectionDate().isBefore(todayPlus2));

        Collection activeCollection =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded);
        assertNotNull(activeCollection);
        assertNotEquals(CollectionStatus.CANCELLED,
                activeCollection.getCollectionStatus());
    }

    @Test
    void testGetSchedulesByCustomer() {
        RecurringSchedule schedule =
                new RecurringSchedule(customer, waste, LocalDate.now(), Frequency.WEEKLY);
        getRecurringScheduleDAO().insert(schedule);

        List<RecurringSchedule> result =
                getRecurringScheduleManager().getSchedulesByCustomer(customer);

        assertEquals(1, result.size());
        assertEquals(customer, result.get(0).getCustomer());
        assertEquals(waste, result.get(0).getWaste());
    }

    @Test
    void testCreateRecurringScheduleCalculatesNextCollectionDate() {
        RecurringSchedule schedule =
                getRecurringScheduleManager().createRecurringSchedule(customer, waste,
                        LocalDate.now(), Frequency.WEEKLY);
        assertNotNull(schedule.getNextCollectionDate());
        WasteSchedule ws = getWasteScheduleDAO().findSchedulebyWaste(waste);
        LocalDate expectedDate = LocalDate.now().plusDays(2);
        while (expectedDate.getDayOfWeek() != ws.getDayOfWeek()) {
            expectedDate = expectedDate.plusDays(1);
        }
        assertEquals(expectedDate, schedule.getNextCollectionDate());
    }

    @Test
    void testUpdateNextDatesUpdatesSchedulesWithPastNextCollectionDate() {
        final int daysInPast = 5;
        LocalDate pastDate = LocalDate.now().minusDays(daysInPast);

        RecurringSchedule schedule =
                new RecurringSchedule(customer, waste, LocalDate.now(), Frequency.WEEKLY);
        schedule.setNextCollectionDate(pastDate);
        schedule.setScheduleStatus(ScheduleStatus.ACTIVE);
        getRecurringScheduleDAO().insert(schedule);

        getRecurringScheduleManager().updateNextDates();

        RecurringSchedule updated =
                getRecurringScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(updated.getNextCollectionDate().isAfter(LocalDate.now()));
    }

    @Test
    void testUpdateNextDatesDoesNotChangeFutureNextCollectionDate() {
        LocalDate futureDate = LocalDate.now().plusDays(10);

        RecurringSchedule schedule =
                new RecurringSchedule(customer, waste, LocalDate.now(), Frequency.WEEKLY);
        schedule.setNextCollectionDate(futureDate);
        schedule.setScheduleStatus(ScheduleStatus.ACTIVE);
        getRecurringScheduleDAO().insert(schedule);

        getRecurringScheduleManager().updateNextDates();

        RecurringSchedule updated =
                getRecurringScheduleDAO().findById(schedule.getScheduleId());
        assertEquals(futureDate, updated.getNextCollectionDate());
    }

}
