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
    private LocalDate startDate;
    private RecurringSchedule schedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "1", "Milano", "Italy");
        customer = new Customer("Mario", "Rossi", location, "mario@rossi.com", "1234567890");
        startDate = LocalDate.now();
        schedule = new RecurringSchedule(customer, Waste.WasteType.PLASTIC, Schedule.ScheduleStatus.ACTIVE, startDate,
                RecurringSchedule.Frequency.WEEKLY);
    }

    @Test
    void testRecurringScheduleGettersAndSetters() {
        assertEquals(customer, schedule.getCustomer());
        assertEquals(Waste.WasteType.PLASTIC, schedule.getWasteType());
        assertEquals(Schedule.ScheduleStatus.ACTIVE, schedule.getStatus());
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
        invalid.setStartDate(LocalDate.now().minusDays(1));
        invalid.setFrequency(null);
        invalid.setCustomer(null);
        invalid.setWasteType(null);
        invalid.setStatus(null);

        Set<ConstraintViolation<RecurringSchedule>> violations = ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Start Date must be today or in the future")));
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
        em.getTransaction().begin();
        em.persist(location);
        em.persist(customer);
        em.persist(schedule);

        em.getTransaction().commit();

        RecurringSchedule found = em.find(RecurringSchedule.class, schedule.getId());
        assertNotNull(found);
        assertEquals(schedule.getFrequency(), found.getFrequency());
        assertEquals(schedule.getStartDate(), found.getStartDate());

        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();

        RecurringSchedule deleted = em.find(RecurringSchedule.class, schedule.getId());
        assertNull(deleted);
    }

}
