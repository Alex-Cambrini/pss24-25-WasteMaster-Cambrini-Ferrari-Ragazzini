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
        plastic = new Waste(Waste.WasteType.PLASTIC, true, false);
        paper = new Waste(Waste.WasteType.PAPER, true, false);

        plasticSchedule = new WasteSchedule(plastic, DayOfWeek.MONDAY);
        paperSchedule = new WasteSchedule(paper, DayOfWeek.WEDNESDAY);

        wasteDAO.insert(plastic);
        wasteDAO.insert(paper);
        wasteScheduleDAO.insert(plasticSchedule);
        wasteScheduleDAO.insert(paperSchedule);
    }

    @Test
    void testFindByWasteType() {
        WasteSchedule resultPlastic = wasteScheduleDAO.findByWasteType(Waste.WasteType.PLASTIC);
        assertNotNull(resultPlastic);
        assertEquals(plasticSchedule.getScheduleId(), resultPlastic.getScheduleId());
        assertEquals(Waste.WasteType.PLASTIC, resultPlastic.getWaste().getType());

        WasteSchedule resultPaper = wasteScheduleDAO.findByWasteType(Waste.WasteType.PAPER);
        assertNotNull(resultPaper);
        assertEquals(paperSchedule.getScheduleId(), resultPaper.getScheduleId());
        assertEquals(Waste.WasteType.PAPER, resultPaper.getWaste().getType());
    }

}
