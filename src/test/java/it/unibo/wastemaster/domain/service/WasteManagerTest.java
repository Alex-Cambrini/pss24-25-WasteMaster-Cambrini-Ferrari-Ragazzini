package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WasteManagerTest extends AbstractDatabaseTest {

    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        waste = new Waste("Plastic", true, false);
    }

    @Test
    void testAddWaste() {
        Waste saved = getWasteManager().addWaste(waste);
        assertNotNull(saved);
        assertEquals("Plastic", saved.getWasteName());

        Waste duplicate = new Waste("Plastic", false, true);
        assertThrows(IllegalArgumentException.class,
                () -> getWasteManager().addWaste(duplicate));

        assertThrows(IllegalArgumentException.class,
                () -> getWasteManager().addWaste(null));
        assertThrows(IllegalArgumentException.class,
                () -> getWasteManager().addWaste(new Waste(null, null, null)));
    }

    @Test
    void testGetAllWastes() {
        assertTrue(getWasteManager().getActiveWastes().isEmpty());

        Waste w1 = new Waste("Glass", true, false);
        Waste w2 = new Waste("Paper", true, false);
        getWasteManager().addWaste(w1);
        getWasteManager().addWaste(w2);

        List<Waste> result = getWasteManager().getActiveWastes();
        assertEquals(2, result.size());

        List<String> names = result.stream().map(Waste::getWasteName).toList();
        assertTrue(names.contains("Glass"));
        assertTrue(names.contains("Paper"));
    }

    @Test
    void testSoftDeleteWaste() {
        Waste saved = getWasteManager().addWaste(waste);
        assertFalse(saved.isDeleted());

        boolean deleted = getWasteManager().softDeleteWaste(saved);
        assertTrue(deleted);
        assertTrue(saved.isDeleted());

        List<Waste> all = getWasteManager().getActiveWastes();
        assertEquals(0, all.size());
        assertFalse(getWasteManager().softDeleteWaste(null));

        Waste temp = new Waste("Organic", true, false);
        assertFalse(getWasteManager().softDeleteWaste(temp));
    }
}
