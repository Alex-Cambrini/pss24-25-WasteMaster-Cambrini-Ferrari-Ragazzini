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
        assertTrue(initialWastes.isEmpty(), "Expected no wastes initially in the database");

        Waste waste1 = new Waste("Glass", true, false);
        Waste waste2 = new Waste("Paper", true, false);

        wasteDAO.insert(waste1);
        wasteDAO.insert(waste2);

        List<Waste> wastes = wasteManager.getAllWastes();
        assertNotNull(wastes);
        assertEquals(2, wastes.size(), "Expected 2 wastes after insertions");

        List<String> names = wastes.stream().map(Waste::getWasteName).toList();
        assertTrue(names.contains(waste1.getWasteName()));
        assertTrue(names.contains(waste2.getWasteName()));
    }
}
