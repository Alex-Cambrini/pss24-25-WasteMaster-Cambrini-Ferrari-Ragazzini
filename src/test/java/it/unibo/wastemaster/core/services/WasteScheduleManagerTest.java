package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.DayOfWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteScheduleManagerTest extends AbstractDatabaseTest {

    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        waste = new Waste("plastic", true, false);
        getWasteDAO().insert(waste);

    }

    @Test
    void testSetupCollectionRoutine() {
        WasteSchedule schedule =
                getWasteScheduleManager().setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        assertNotNull(schedule);
        assertEquals(waste, schedule.getWaste());
        assertEquals(DayOfWeek.MONDAY, schedule.getDayOfWeek());

        assertTrue(schedule.getScheduleId() > 0);

        WasteSchedule found = getWasteScheduleDAO().findSchedulebyWaste(waste);
        assertNotNull(found);
        assertEquals(DayOfWeek.MONDAY, found.getDayOfWeek());
    }

    @Test
    void testChangeCollectionDay() {
        WasteSchedule schedule =
                getWasteScheduleManager().setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        WasteSchedule updated =
                getWasteScheduleManager().changeCollectionDay(schedule, DayOfWeek.FRIDAY);

        assertNotNull(updated);
        assertEquals(DayOfWeek.FRIDAY, updated.getDayOfWeek());

        assertEquals(waste, updated.getWaste());

        WasteSchedule found = getWasteScheduleDAO().findSchedulebyWaste(waste);
        assertEquals(DayOfWeek.FRIDAY, found.getDayOfWeek());
    }

    @Test
    void testgetWasteScheduleByWaste() {
        getWasteScheduleManager().setupCollectionRoutine(waste, DayOfWeek.TUESDAY);

        WasteSchedule found = getWasteScheduleManager().getWasteScheduleByWaste(waste);

        assertNotNull(found);
        assertEquals(DayOfWeek.TUESDAY, found.getDayOfWeek());
    }

    @Test
    void testFindScheduleByWasteWithNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            getWasteScheduleManager().getWasteScheduleByWaste(null);
        });
        assertTrue(ex.getMessage().contains("WasteType cannot be null"));
    }

    @Test
    void testFindScheduleByWasteNotFound() {
        Waste glass = new Waste("glass", true, false);
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            getWasteScheduleManager().getWasteScheduleByWaste(glass);
        });
        assertTrue(ex.getMessage().contains("No WasteSchedule found for waste type"));
    }
}
