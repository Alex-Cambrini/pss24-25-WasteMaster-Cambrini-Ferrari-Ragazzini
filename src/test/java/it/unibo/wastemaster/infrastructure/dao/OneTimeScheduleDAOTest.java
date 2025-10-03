package it.unibo.wastemaster.infrastructure.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.util.Optional;
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

        Optional<OneTimeSchedule> foundOpt =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(foundOpt.isPresent());
        OneTimeSchedule found = foundOpt.get();
        assertEquals(schedule.getScheduleId(), found.getScheduleId());
        assertEquals(schedule.getCustomer(), found.getCustomer());
        assertEquals(schedule.getWaste(), found.getWaste());
    }

    @Test
    void deleteTest() {
        getOneTimeScheduleDAO().insert(schedule);

        Optional<OneTimeSchedule> toDeleteOpt =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(toDeleteOpt.isPresent());

        getOneTimeScheduleDAO().delete(toDeleteOpt.get());

        Optional<OneTimeSchedule> deletedOpt =
                getOneTimeScheduleDAO().findById(schedule.getScheduleId());
        assertTrue(deletedOpt.isEmpty());
    }
}
