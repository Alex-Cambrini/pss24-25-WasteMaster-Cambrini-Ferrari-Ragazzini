package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RecurringScheduleTest {

    private Customer customer;
    private Location location;
    private LocalDate startDate;
    private RecurringSchedule schedule;

    @BeforeEach
    void setUp() {
        location = new Location("Via Roma", "1", "Milano", "Italy");
        customer = new Customer("Mario", "Rossi", location, "mario@rossi.com", "1234567890");
        startDate = LocalDate.now();
        schedule = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, Schedule.ScheduleStatus.ACTIVE, startDate,
                RecurringSchedule.Frequency.WEEKLY);
    }

    @Test
    void testGetAndSetStartDate() {
        assertEquals(startDate, schedule.getStartDate());
        LocalDate newDate = startDate.plusDays(3);
        schedule.setStartDate(newDate);
        assertEquals(newDate, schedule.getStartDate());

        schedule.setStartDate(null);
        assertNull(schedule.getStartDate());
    }

    @Test
    void testGetAndSetFrequency() {
        assertEquals(RecurringSchedule.Frequency.WEEKLY, schedule.getFrequency());
        schedule.setFrequency(RecurringSchedule.Frequency.MONTHLY);
        assertEquals(RecurringSchedule.Frequency.MONTHLY, schedule.getFrequency());

        schedule.setFrequency(null);
        assertNull(schedule.getFrequency());
    }

    @Test
    void testGetAndSetNextCollectionDate() {
        assertNull(schedule.getNextCollectionDate());
        LocalDate nextDate = startDate.plusWeeks(1);
        schedule.setNextCollectionDate(nextDate);
        assertEquals(nextDate, schedule.getNextCollectionDate());
    }
}
