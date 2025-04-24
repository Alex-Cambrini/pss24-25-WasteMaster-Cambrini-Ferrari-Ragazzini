package it.unibo.wastemaster.core.dao;

import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.DateUtils;

public class RecurringScheduleDAOTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private RecurringSchedule schedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        schedule = new RecurringSchedule(
                customer,
                Waste.WasteType.PLASTIC,
                DateUtils.getCurrentDate(),
                RecurringSchedule.Frequency.WEEKLY);
        schedule.setNextCollectionDate(DateUtils.getCurrentDate().plusDays(1));

        locationDAO.insert(location);
        customerDAO.insert(customer);
        recurringScheduleDAO.insert(schedule);
    }

}
