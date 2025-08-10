package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OneTimeScheduleTest extends AbstractDatabaseTest {

    private OneTimeSchedule schedule;
    private Customer customer;
    private LocalDate pickupDate;
    private Waste organic;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        Location location = new Location("Via Dante", "5", "Roma", "00100");
        customer =
                new Customer("Luca", "Verdi", location, "luca@example.com", "3456789012");
        organic = new Waste("organic", true, false);
        pickupDate = LocalDate.now();
        schedule = new OneTimeSchedule(customer, organic, pickupDate);
    }

    @Test
    void testGetterAndSetter() {
        assertEquals(pickupDate, schedule.getPickupDate());

        LocalDate newDate = pickupDate.plusDays(1);
        schedule.setPickupDate(newDate);
        assertEquals(newDate, schedule.getPickupDate());
    }

    @Test
    void testInvalidSchedule() {
        OneTimeSchedule invalid = new OneTimeSchedule();
        invalid.setPickupDate(LocalDate.now().minusDays(2));

        Set<ConstraintViolation<OneTimeSchedule>> violations =
                ValidateUtils.VALIDATOR.validate(invalid);
        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("customer")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("waste")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("status")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("pickupDate")));
    }

    @Test
    void testValidSchedule() {
        Set<ConstraintViolation<OneTimeSchedule>> violations =
                ValidateUtils.VALIDATOR.validate(schedule);
        assertTrue(violations.isEmpty(),
                "Expected no validation errors for a valid OneTimeSchedule");
    }

    @Test
    void testPersistence() {
        getCustomerDAO().insert(customer);
        getWasteDAO().insert(organic);
        getOneTimeScheduleDAO().insert(schedule);
        int scheduleId = schedule.getScheduleId();
        OneTimeSchedule found = getOneTimeScheduleDAO().findById(scheduleId);
        assertNotNull(found);
        assertEquals(pickupDate, found.getPickupDate());
        assertEquals(customer.getEmail(), found.getCustomer().getEmail());

        getOneTimeScheduleDAO().delete(found);
        OneTimeSchedule deleted = getOneTimeScheduleDAO().findById(scheduleId);
        assertNull(deleted);
    }

    @Test
    void testToString() {
        String toStringOutput = schedule.toString();
        assertNotNull(toStringOutput);
        assertTrue(toStringOutput.contains("ONE_TIME Schedule"));
        assertTrue(toStringOutput.contains(customer.getName()));
        assertTrue(toStringOutput.contains(organic.getWasteName()));
        assertTrue(toStringOutput.contains(pickupDate.toString()));
    }
}
