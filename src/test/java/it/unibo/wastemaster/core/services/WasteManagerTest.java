package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WasteManagerTest extends AbstractDatabaseTest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		em.getTransaction().begin();
	}

	@Test
	void testGetAllWastes() {
		List<Waste> initialWastes = wasteManager.getAllWastes();
		assertNotNull(initialWastes);
		assertTrue(initialWastes.isEmpty());

		wasteManager.addWaste(new Waste("Glass", true, false));
		wasteManager.addWaste(new Waste("Paper", true, false));

		List<Waste> wastes = wasteManager.getAllWastes();
		assertEquals(2, wastes.size());
	}

	@Test
	void testAddWaste() {
		Waste waste = new Waste("Metal", true, false);
		Waste saved = wasteManager.addWaste(waste);
		assertNotNull(saved);
		assertEquals("Metal", saved.getWasteName());
		assertTrue(saved.getIsRecyclable());
		assertFalse(saved.getIsDangerous());

		Waste duplicate = new Waste("Metal", false, true);
		assertThrows(IllegalArgumentException.class, () -> wasteManager.addWaste(duplicate));
	}

	@Test
	void testSoftDeleteWaste() {
		Waste waste = new Waste("Organic", true, false);
		wasteManager.addWaste(waste);

		boolean deleted = wasteManager.softDeleteWaste(waste);
		assertTrue(deleted);

		Waste found = wasteDAO.findById(waste.getWasteId());
		assertNotNull(found);
		assertTrue(found.isDeleted());

		List<Waste> all = wasteManager.getAllWastes();
		assertTrue(all.isEmpty());
	}
}
