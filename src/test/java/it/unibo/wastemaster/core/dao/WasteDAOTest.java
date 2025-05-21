package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class WasteDAOTest extends AbstractDatabaseTest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		em.getTransaction().begin();
	}

	@Test
	void testExistsByName() {
		Waste waste = new Waste("Organic", true, false);
		wasteDAO.insert(waste);

		boolean exists = wasteDAO.existsByName("Organic");
		assertTrue(exists);

		boolean notExists = wasteDAO.existsByName("Paper");
		assertFalse(notExists);
	}

	@Test
	void testExistsByNameIgnoresDeleted() {
		Waste waste = new Waste("Oil", false, true);
		wasteDAO.insert(waste);
		waste.delete();
		wasteDAO.update(waste);

		boolean exists = wasteDAO.existsByName("Oil");
		assertFalse(exists);
	}
}
