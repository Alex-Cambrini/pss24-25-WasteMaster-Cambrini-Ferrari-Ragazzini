package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.RecurringSchedule;
import it.unibo.wastemaster.domain.model.RecurringSchedule.Frequency;
import it.unibo.wastemaster.domain.model.Schedule.ScheduleStatus;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

        Optional<Collection> activeCollectionOpt =
                getCollectionManager().getActiveCollectionByRecurringSchedule(s2);
        assertTrue(activeCollectionOpt.isPresent());
        Collection associatedCollection = activeCollectionOpt.get();
        int associatedCollectionId = associatedCollection.getCollectionId();

        assertNotEquals(CollectionStatus.CANCELLED,
                associatedCollection.getCollectionStatus());
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(s2,
                ScheduleStatus.PAUSED));

        Optional<RecurringSchedule> reloaded2Opt =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        assertTrue(reloaded2Opt.isPresent());
        RecurringSchedule reloaded2 = reloaded2Opt.get();

        Optional<Collection> associatedCollectionOpt =
                getCollectionDAO().findById(associatedCollectionId);
        assertTrue(associatedCollectionOpt.isPresent());
        associatedCollection = associatedCollectionOpt.get();

        assertEquals(ScheduleStatus.PAUSED, reloaded2.getScheduleStatus());
        assertEquals(CollectionStatus.CANCELLED,
                associatedCollection.getCollectionStatus());

        // PAUSED -> ACTIVE: recalc next date, update status, generate new collection
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(reloaded2,
                ScheduleStatus.ACTIVE));

        Optional<RecurringSchedule> reloaded3Opt =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        assertTrue(reloaded3Opt.isPresent());
        RecurringSchedule reloaded3 = reloaded3Opt.get();

        assertEquals(ScheduleStatus.ACTIVE, reloaded3.getScheduleStatus());

        activeCollectionOpt =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded3);
        assertTrue(activeCollectionOpt.isPresent());
        associatedCollection = activeCollectionOpt.get();

        assertNotNull(associatedCollection);
        assertNotNull(reloaded3.getNextCollectionDate());

        // ACTIVE -> CANCELLED: update status and soft delete collection
        assertTrue(getRecurringScheduleManager().updateStatusRecurringSchedule(reloaded3,
                ScheduleStatus.CANCELLED));

        Optional<RecurringSchedule> reloaded4Opt =
                getRecurringScheduleDAO().findById(s2.getScheduleId());
        assertTrue(reloaded4Opt.isPresent());
        RecurringSchedule reloaded4 = reloaded4Opt.get();

        activeCollectionOpt =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded4);
        assertTrue(activeCollectionOpt.isEmpty());

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

        Optional<RecurringSchedule> reloadedOpt =
                getRecurringScheduleDAO().findById(s3.getScheduleId());
        assertTrue(reloadedOpt.isPresent());
        RecurringSchedule reloaded = reloadedOpt.get();
        assertEquals(Frequency.MONTHLY, reloaded.getFrequency());

        LocalDate todayPlus2 = LocalDate.now().plusDays(2);
        assertFalse(reloaded.getNextCollectionDate().isBefore(todayPlus2));

        Optional<Collection> activeCollectionOpt =
                getCollectionManager().getActiveCollectionByRecurringSchedule(reloaded);
        assertTrue(activeCollectionOpt.isPresent());
        Collection activeCollection = activeCollectionOpt.get();
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

        Optional<RecurringSchedule> updatedOpt =
                getRecurringScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(updatedOpt.isPresent());
        RecurringSchedule updated = updatedOpt.get();

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

        Optional<RecurringSchedule> updatedOpt =
                getRecurringScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(updatedOpt.isPresent());
        RecurringSchedule updated = updatedOpt.get();

        assertEquals(futureDate, updated.getNextCollectionDate());
    }

}
