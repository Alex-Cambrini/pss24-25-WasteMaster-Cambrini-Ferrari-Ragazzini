package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.DayOfWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for WasteScheduleDAO.
 */
class WasteScheduleDAOTest extends AbstractDatabaseTest {

    private Waste plastic;
    private Waste paper;
    private WasteSchedule plasticSchedule;
    private WasteSchedule paperSchedule;

    /**
     * Initializes test data before each test.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        plastic = new Waste("plastic", true, false);
        paper = new Waste("paper", true, false);

        plasticSchedule = new WasteSchedule(plastic, DayOfWeek.MONDAY);
        paperSchedule = new WasteSchedule(paper, DayOfWeek.WEDNESDAY);

        getWasteDAO().insert(plastic);
        getWasteDAO().insert(paper);
        getWasteScheduleDAO().insert(plasticSchedule);
        getWasteScheduleDAO().insert(paperSchedule);
    }

    @Test
    void testFindByWasteType() {
        WasteSchedule resultPlastic = getWasteScheduleDAO().findSchedulebyWaste(plastic);
        assertNotNull(resultPlastic);
        assertEquals(plasticSchedule.getScheduleId(), resultPlastic.getScheduleId());
        assertEquals(plastic, resultPlastic.getWaste());

        WasteSchedule resultPaper = getWasteScheduleDAO().findSchedulebyWaste(paper);
        assertNotNull(resultPaper);
        assertEquals(paperSchedule.getScheduleId(), resultPaper.getScheduleId());
        assertEquals(paper, resultPaper.getWaste());
    }
}
