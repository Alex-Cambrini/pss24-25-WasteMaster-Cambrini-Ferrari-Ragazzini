package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RecurringScheduleTest {

    private Customer customer;
    private Location location;
    private Date startDate;
    private RecurringSchedule schedule;

    @BeforeEach
    void setUp() {
        location = new Location("Via Roma", "1", "Milano", "Italy");
        customer = new Customer("Mario", "Rossi", location, "mario@rossi.com", "1234567890");
        startDate = new Date();
        schedule = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, Schedule.ScheduleStatus.ACTIVE, startDate,
                RecurringSchedule.Frequency.WEEKLY);
    }

    @Test
    void testGetAndSetStartDate() {
        assertEquals(startDate, schedule.getStartDate());
        Date newDate = new Date(System.currentTimeMillis() + 100000);
        schedule.setStartDate(newDate);
        assertEquals(newDate, schedule.getStartDate());
    }

    @Test
    void testGetAndSetFrequency() {
        assertEquals(RecurringSchedule.Frequency.WEEKLY, schedule.getFrequency());
        schedule.setFrequency(RecurringSchedule.Frequency.MONTHLY);
        assertEquals(RecurringSchedule.Frequency.MONTHLY, schedule.getFrequency());
    }

    @Test
    void testGetAndSetNextCollectionDate() {
        assertNull(schedule.getNextCollectionDate());
        Date nextDate = new Date();
        schedule.setNextCollectionDate(nextDate);
        assertEquals(nextDate, schedule.getNextCollectionDate());
    }

    @Test
    void testConstructorRejectsNulls() {
        assertThrows(IllegalArgumentException.class, () -> new RecurringSchedule(customer, Waste.WasteType.GLASS,
                Schedule.ScheduleStatus.ACTIVE, null, RecurringSchedule.Frequency.MONTHLY));

        assertThrows(IllegalArgumentException.class, () -> new RecurringSchedule(customer, Waste.WasteType.GLASS,
                Schedule.ScheduleStatus.ACTIVE, new Date(), null));
    }

    @Test
    void testSettersRejectNulls() {
        assertThrows(IllegalArgumentException.class, () -> schedule.setStartDate(null));
        assertThrows(IllegalArgumentException.class, () -> schedule.setFrequency(null));
    }
}