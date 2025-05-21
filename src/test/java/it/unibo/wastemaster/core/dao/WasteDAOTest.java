package it.unibo.wastemaster.core.dao;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WasteDAOTest extends AbstractDatabaseTest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		em.getTransaction().begin();
	}

	@Test
	void testFindAllExcludesDeleted() {
		Waste waste1 = new Waste("Plastic", true, false);
		Waste waste2 = new Waste("Glass", true, false);
		Waste waste3 = new Waste("Metal", false, true);
		waste3.delete();

		wasteDAO.insert(waste1);
		wasteDAO.insert(waste2);
		wasteDAO.insert(waste3);
		wasteDAO.update(waste3);

		List<Waste> result = wasteDAO.findAll();
		assertEquals(2, result.size());

		List<String> names = result.stream().map(Waste::getWasteName).toList();
		assertTrue(names.contains("Plastic"));
		assertTrue(names.contains("Glass"));
		assertFalse(names.contains("Metal"));
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
