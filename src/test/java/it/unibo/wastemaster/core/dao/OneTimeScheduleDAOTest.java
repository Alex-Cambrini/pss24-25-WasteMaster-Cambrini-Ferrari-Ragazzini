package it.unibo.wastemaster.core.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Waste;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OneTimeScheduleDAOTest extends AbstractDatabaseTest {

    private Location location1;
    private Location location2;
    private Customer customer1;
    private Customer customer2;
    private Waste waste;
    private LocalDate date;
    private OneTimeSchedule schedule;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        date = LocalDate.now();
        waste = new Waste("PLASTICA", true, false);
        location1 = new Location("Via Roma", "10", "Bologna", "40100");
        location2 = new Location("Via Milano", "32", "Torino", "80700");

        customer1 = new Customer("Mario", "Rossi", location1, "mario.rossi@example.com",
                "1234567890");
        customer2 = new Customer("Luca", "Verdi", location2, "luca.verdi@example.com",
                "1234567890");

        getEntityManager().getTransaction().begin();
        getWasteDAO().insert(waste);
        getCustomerDAO().insert(customer1);
        getCustomerDAO().insert(customer2);

        schedule = new OneTimeSchedule(customer1, waste, date);
    }

    @Test
    void insertTest() {
        getOneTimeScheduleDAO().insert(schedule);
        assertTrue(schedule.getScheduleId() > 0);
    }

    @Test
    void findTest() {
        getOneTimeScheduleDAO().insert(schedule);
        OneTimeSchedule found =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        assertNotNull(found);
        assertEquals(schedule.getScheduleId(), found.getScheduleId());
        assertEquals(schedule.getCustomer(), found.getCustomer());
        assertEquals(schedule.getWaste(), found.getWaste());
    }

    @Test
    void deleteTest() {
        getOneTimeScheduleDAO().insert(schedule);
        OneTimeSchedule toDelete =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        getOneTimeScheduleDAO().delete(toDelete);
        OneTimeSchedule deleted =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        assertNull(deleted);
    }
}
