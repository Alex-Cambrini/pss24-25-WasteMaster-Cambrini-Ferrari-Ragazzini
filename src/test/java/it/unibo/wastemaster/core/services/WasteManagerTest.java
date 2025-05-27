package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Waste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WasteManagerTest extends AbstractDatabaseTest {

    private Waste waste;

    @BeforeEach
    public void setUp() {
        super.setUp();
        em.getTransaction().begin();
        waste = new Waste("Plastic", true, false);
    }

    @Test
    void testAddWaste() {
        Waste saved = wasteManager.addWaste(waste);
        assertNotNull(saved);
        assertEquals("Plastic", saved.getWasteName());

        Waste duplicate = new Waste("Plastic", false, true);
        assertThrows(IllegalArgumentException.class,
                () -> wasteManager.addWaste(duplicate));

        assertThrows(IllegalArgumentException.class, () -> wasteManager.addWaste(null));
        assertThrows(IllegalArgumentException.class,
                () -> wasteManager.addWaste(new Waste(null, null, null)));
    }

    @Test
    void testGetAllWastes() {
        assertTrue(wasteManager.getActiveWastes().isEmpty());

        Waste w1 = new Waste("Glass", true, false);
        Waste w2 = new Waste("Paper", true, false);
        wasteManager.addWaste(w1);
        wasteManager.addWaste(w2);

        List<Waste> result = wasteManager.getActiveWastes();
        assertEquals(2, result.size());

        List<String> names = result.stream().map(Waste::getWasteName).toList();
        assertTrue(names.contains("Glass"));
        assertTrue(names.contains("Paper"));
    }

    @Test
    void testSoftDeleteWaste() {
        Waste saved = wasteManager.addWaste(waste);
        assertFalse(saved.isDeleted());

        boolean deleted = wasteManager.softDeleteWaste(saved);
        assertTrue(deleted);
        assertTrue(saved.isDeleted());

        List<Waste> all = wasteManager.getActiveWastes();
        assertEquals(0, all.size());
        assertFalse(wasteManager.softDeleteWaste(null));

        Waste temp = new Waste("Organic", true, false);
        assertFalse(wasteManager.softDeleteWaste(temp));
    }

}
