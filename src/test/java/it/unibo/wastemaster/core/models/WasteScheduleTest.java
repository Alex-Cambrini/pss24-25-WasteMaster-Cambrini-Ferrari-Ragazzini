package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import jakarta.validation.ConstraintViolation;
import java.time.DayOfWeek;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteScheduleTest extends AbstractDatabaseTest {

    private Waste plastic;
    private WasteSchedule schedule;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        plastic = new Waste("plastic", true, false);
        schedule = new WasteSchedule(plastic, DayOfWeek.FRIDAY);
    }

    @Test
    void testGetterAndSetter() {
        assertEquals(plastic, schedule.getWaste());
        assertEquals(DayOfWeek.FRIDAY, schedule.getDayOfWeek());

        Waste glass = new Waste("glass", false, false);
        schedule.setWaste(glass);
        assertEquals(glass, schedule.getWaste());

        schedule.setDayOfWeek(DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.SUNDAY, schedule.getDayOfWeek());
    }

    @Test
    void testInvalidWasteSchedule() {
        WasteSchedule invalid = new WasteSchedule();
        Set<ConstraintViolation<WasteSchedule>> violations =
                ValidateUtils.VALIDATOR.validate(invalid);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("waste")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("dayOfWeek")));
    }

    @Test
    void testValidWasteSchedule() {
        Set<ConstraintViolation<WasteSchedule>> violations =
                ValidateUtils.VALIDATOR.validate(schedule);
        assertTrue(
                violations.isEmpty(),
                "Expected no validation errors for a valid WasteSchedule"
        );
    }

    @Test
    void testPersistence() {
        getWasteDAO().insert(plastic);
        getWasteScheduleDAO().insert(schedule);
        int scheduleId = schedule.getScheduleId();

        Optional<WasteSchedule> found = getWasteScheduleDAO().findById(scheduleId);
        assertTrue(found.isPresent());
        WasteSchedule foundSchedule = found.get();
        assertEquals(plastic, foundSchedule.getWaste());
        assertEquals(schedule.getDayOfWeek(), foundSchedule.getDayOfWeek());

        getWasteScheduleDAO().delete(foundSchedule);
        Optional<WasteSchedule> deleted = getWasteScheduleDAO().findById(scheduleId);
        assertFalse(deleted.isPresent());
    }

}
