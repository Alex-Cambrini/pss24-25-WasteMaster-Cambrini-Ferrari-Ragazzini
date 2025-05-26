package it.unibo.wastemaster.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import jakarta.validation.ConstraintViolation;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecurringScheduleTest extends AbstractDatabaseTest {

    private Customer customer;
    private Location location;
    private Waste plastic;
    private LocalDate startDate;
    private RecurringSchedule schedule;


    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "1", "Milano", "20100");
        customer = new Customer("Mario", "Rossi", location, "mario@rossi.com", "1234567890");
        plastic = new Waste("PLASTICA", true, false);
        startDate = LocalDate.now();
        schedule = new RecurringSchedule(customer, plastic, startDate,
                RecurringSchedule.Frequency.WEEKLY);
        schedule.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);
    }

    @Test
    void testRecurringScheduleGettersAndSetters() {
        assertEquals(customer, schedule.getCustomer());
        assertEquals(plastic, schedule.getWaste());
        assertEquals(Schedule.ScheduleStatus.ACTIVE, schedule.getScheduleStatus());
        assertEquals(startDate, schedule.getStartDate());
        assertEquals(RecurringSchedule.Frequency.WEEKLY, schedule.getFrequency());
        assertNull(schedule.getNextCollectionDate());

        LocalDate newStartDate = LocalDate.now().plusDays(5);
        RecurringSchedule.Frequency newFrequency = RecurringSchedule.Frequency.MONTHLY;
        LocalDate nextDate = LocalDate.now().plusWeeks(1);

        schedule.setStartDate(newStartDate);
        schedule.setFrequency(newFrequency);
        schedule.setNextCollectionDate(nextDate);

        assertEquals(newStartDate, schedule.getStartDate());
        assertEquals(newFrequency, schedule.getFrequency());
        assertEquals(nextDate, schedule.getNextCollectionDate());
    }

    @Test
    void testRecurringScheduleValidation() {
        RecurringSchedule invalid = new RecurringSchedule();
        invalid.setFrequency(null);
        invalid.setCustomer(null);
        invalid.setWaste(null);
        invalid.setScheduleStatus(null);

        Set<ConstraintViolation<RecurringSchedule>> violations = ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Frequency cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Customer cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("WasteType cannot be null")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Status cannot be null")));
    }

    @Test
    public void testValidSchedule() {
        Set<ConstraintViolation<RecurringSchedule>> violations = ValidateUtils.VALIDATOR.validate(schedule);
        assertTrue(violations.isEmpty(), "Validation failed for a valid RecurringSchedule: " + violations);
    }

    @Test
    void testRecurringSchedulePersistence() {
        customerDAO.insert(customer);
        wasteDAO.insert(plastic);
        recurringScheduleDAO.insert(schedule);
        int scheduleId = schedule.getScheduleId();
        RecurringSchedule found = recurringScheduleDAO.findById(scheduleId);
        assertNotNull(found);
        assertEquals(schedule.getFrequency(), found.getFrequency());
        assertEquals(schedule.getStartDate(), found.getStartDate());

        recurringScheduleDAO.delete(found);
        RecurringSchedule deleted = recurringScheduleDAO.findById(scheduleId);
        assertNull(deleted);
    }

    @Test
    void testToString() {
        String result = schedule.toString();
        assertNotNull(result);
        assertTrue(result.contains("RECURRING Schedule"));
        assertTrue(result.contains(customer.getName()));
        assertTrue(result.contains(plastic.getWasteName()));
        assertTrue(result.contains(schedule.getStartDate().toString()));
    }

}
