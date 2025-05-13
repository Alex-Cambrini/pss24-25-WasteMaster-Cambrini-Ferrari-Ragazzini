package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.DayOfWeek;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

public class WasteScheduleDAOTest extends AbstractDatabaseTest {

    private Waste plastic;
    private Waste paper;
    private WasteSchedule plasticSchedule;
    private WasteSchedule paperSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        plastic = new Waste("plastic", true, false);
        paper = new Waste("paper", true, false);

        plasticSchedule = new WasteSchedule(plastic, DayOfWeek.MONDAY);
        paperSchedule = new WasteSchedule(paper, DayOfWeek.WEDNESDAY);

        wasteDAO.insert(plastic);
        wasteDAO.insert(paper);
        wasteScheduleDAO.insert(plasticSchedule);
        wasteScheduleDAO.insert(paperSchedule);
    }

    @Test
    void testFindByWasteType() {
        WasteSchedule resultPlastic = wasteScheduleDAO.findSchedulebyWaste(plastic);
        assertNotNull(resultPlastic);
        assertEquals(plasticSchedule.getScheduleId(), resultPlastic.getScheduleId());
        assertEquals(plastic, resultPlastic.getWaste());

        WasteSchedule resultPaper = wasteScheduleDAO.findSchedulebyWaste(paper);
        assertNotNull(resultPaper);
        assertEquals(paperSchedule.getScheduleId(), resultPaper.getScheduleId());
        assertEquals(paper, resultPaper.getWaste());
    }

}
