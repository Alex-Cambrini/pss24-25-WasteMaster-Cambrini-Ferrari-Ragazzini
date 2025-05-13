package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteScheduleManagerTest extends AbstractDatabaseTest {

    private WasteScheduleManager wasteScheduleManager;
    private Waste waste;

    @BeforeEach
    public void setUp() {
        super.setUp();
        em.getTransaction().begin();

        waste = new Waste("plastic", true, false);
        wasteDAO.insert(waste);

        wasteScheduleManager = new WasteScheduleManager(wasteScheduleDAO);
    }

    @Test
    void testSetupCollectionRoutine() {
        WasteSchedule schedule = wasteScheduleManager.setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        assertNotNull(schedule);
        assertEquals(waste, schedule.getWaste());
        assertEquals(DayOfWeek.MONDAY, schedule.getDayOfWeek());

        assertTrue(schedule.getScheduleId() > 0);

        WasteSchedule found = wasteScheduleDAO.findSchedulebyWaste(waste);
        assertNotNull(found);
        assertEquals(DayOfWeek.MONDAY, found.getDayOfWeek());
    }

    @Test
    void testChangeCollectionDay() {
        WasteSchedule schedule = wasteScheduleManager.setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        WasteSchedule updated = wasteScheduleManager.changeCollectionDay(schedule, DayOfWeek.FRIDAY);

        assertNotNull(updated);
        assertEquals(DayOfWeek.FRIDAY, updated.getDayOfWeek());

        assertEquals(waste, updated.getWaste());

        WasteSchedule found = wasteScheduleDAO.findSchedulebyWaste(waste);
        assertEquals(DayOfWeek.FRIDAY, found.getDayOfWeek());
    }

    @Test
    void testgetWasteScheduleByWaste() {
        wasteScheduleManager.setupCollectionRoutine(waste, DayOfWeek.TUESDAY);

        WasteSchedule found = wasteScheduleManager.getWasteScheduleByWaste(waste);

        assertNotNull(found);
        assertEquals(DayOfWeek.TUESDAY, found.getDayOfWeek());
    }

    @Test
    void testgetWasteScheduleByWaste_NullInput() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            wasteScheduleManager.getWasteScheduleByWaste(null);
        });
        assertTrue(ex.getMessage().contains("WasteType cannot be null"));
    }

    @Test
    void testgetWasteScheduleByWaste_NotFound() {
        Waste glass = new Waste("glass", true, false);
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            wasteScheduleManager.getWasteScheduleByWaste(glass);
        });
        assertTrue(ex.getMessage().contains("No WasteSchedule found for waste type"));
    }
}