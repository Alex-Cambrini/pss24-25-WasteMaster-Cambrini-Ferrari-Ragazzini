package it.unibo.wastemaster.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import it.unibo.wastemaster.domain.service.WasteScheduleManager;
import java.time.DayOfWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteScheduleManagerTest extends AbstractDatabaseTest {

    private WasteScheduleManager wasteScheduleManager;
    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();

        waste = new Waste("plastic", true, false);
        getWasteDAO().insert(waste);

        wasteScheduleManager = new WasteScheduleManager(getWasteScheduleDAO());
    }

    @Test
    void testSetupCollectionRoutine() {
        WasteSchedule schedule =
                wasteScheduleManager.setupCollectionRoutine(waste, DayOfWeek.MONDAY);

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
                wasteScheduleManager.setupCollectionRoutine(waste, DayOfWeek.MONDAY);

        WasteSchedule updated =
                wasteScheduleManager.changeCollectionDay(schedule, DayOfWeek.FRIDAY);

        assertNotNull(updated);
        assertEquals(DayOfWeek.FRIDAY, updated.getDayOfWeek());

        assertEquals(waste, updated.getWaste());

        WasteSchedule found = getWasteScheduleDAO().findSchedulebyWaste(waste);
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
    void testFindScheduleByWasteWithNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            wasteScheduleManager.getWasteScheduleByWaste(null);
        });
        assertTrue(ex.getMessage().contains("WasteType cannot be null"));
    }

    @Test
    void testFindScheduleByWasteNotFound() {
        Waste glass = new Waste("glass", true, false);
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            wasteScheduleManager.getWasteScheduleByWaste(glass);
        });
        assertTrue(ex.getMessage().contains("No WasteSchedule found for waste type"));
    }
}
