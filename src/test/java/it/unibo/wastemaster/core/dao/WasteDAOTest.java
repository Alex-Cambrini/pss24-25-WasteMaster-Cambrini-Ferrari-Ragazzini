package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for WasteDAO.
 */
class WasteDAOTest extends AbstractDatabaseTest {

    /**
     * Begins transaction before each test.
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        getEntityManager().getTransaction().begin();
    }

    @Test
    void testExistsByName() {
        Waste waste = new Waste("Organic", true, false);
        getWasteDAO().insert(waste);

        boolean exists = getWasteDAO().existsByName("Organic");
        assertTrue(exists);

        boolean notExists = getWasteDAO().existsByName("Paper");
        assertFalse(notExists);
    }

    @Test
    void testExistsByNameIgnoresDeleted() {
        Waste waste = new Waste("Oil", false, true);
        getWasteDAO().insert(waste);
        waste.delete();
        getWasteDAO().update(waste);

        boolean exists = getWasteDAO().existsByName("Oil");
        assertFalse(exists);
    }
}
