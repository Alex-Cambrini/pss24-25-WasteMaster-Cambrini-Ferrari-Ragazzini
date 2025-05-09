package it.unibo.wastemaster.core.dao;


import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.utils.DateUtils;

public class OneTimeScheduleDAOTest extends AbstractDatabaseTest {

    private Location location;
    private Customer customer;
    private LocalDate date;
    private Waste.WasteType wasteType;
    private OneTimeSchedule oneTimeSchedule;
    private RecurringSchedule recurringSchedule;
    private Collection collection1;
    private Collection collection2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        date = new DateUtils().getCurrentDate();
        wasteType = Waste.WasteType.PLASTIC;

        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");

        oneTimeSchedule = new OneTimeSchedule(customer, wasteType, date);
        recurringSchedule = new RecurringSchedule(customer, wasteType, date, RecurringSchedule.Frequency.WEEKLY);

        collection1 = new Collection(oneTimeSchedule);
        collection2 = new Collection(recurringSchedule);

        locationDAO.insert(location);
        customerDAO.insert(customer);
        oneTimeScheduleDAO.insert(oneTimeSchedule);
        recurringScheduleDAO.insert(recurringSchedule);
        collectionDAO.insert(collection1);
        collection2.setCollectionDate(date);
        collectionDAO.insert(collection2);
    }
}
