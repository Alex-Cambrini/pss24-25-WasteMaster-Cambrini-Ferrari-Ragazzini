package it.unibo.wastemaster.core.dao;

import java.time.DayOfWeek;

import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

public class WasteScheduleDAOTest extends AbstractDatabaseTest {

    private Waste plastic;
	private WasteSchedule plasticSchedule;

	@BeforeEach
	public void setUp() {
		super.setUp();

		plastic = new Waste(Waste.WasteType.PLASTIC, true, false);
		plasticSchedule = new WasteSchedule(plastic, DayOfWeek.MONDAY);

		em.getTransaction().begin();
		em.persist(plastic);
		wasteScheduleDAO.insert(plasticSchedule);
		em.getTransaction().commit();
	}
}
