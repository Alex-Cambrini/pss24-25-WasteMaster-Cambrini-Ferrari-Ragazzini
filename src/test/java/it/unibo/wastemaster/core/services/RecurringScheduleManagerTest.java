package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.utils.DateUtils;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.models.Waste;
import it.unibo.wastemaster.core.models.WasteSchedule;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;

class RecurringScheduleManagerTest extends AbstractDatabaseTest {
    private Location location;
    private Customer customer;
    private Waste waste;
    private WasteSchedule wasteSchedule;

    @BeforeEach
    public void setUp() {
        super.setUp();
        location = new Location("Via Roma", "10", "Bologna", "40100");
        customer = new Customer("Mario", "Rossi", location, "mario.rossi@example.com", "1234567890");
        waste = new Waste("glass", true, false);
        wasteDAO.insert(waste);
        wasteSchedule = new WasteSchedule(waste, DayOfWeek.MONDAY);
        customerDAO.insert(customer);
        wasteScheduleDAO.insert(wasteSchedule);
    }

    @Test
    void testCreateRecurringSchedule() {
        recurringScheduleManager.createRecurringSchedule(customer, waste, dateUtils.getCurrentDate(),
                Frequency.WEEKLY);
        recurringScheduleManager.createRecurringSchedule(customer, waste, dateUtils.getCurrentDate(),
                Frequency.MONTHLY);

        List<RecurringSchedule> schedules = recurringScheduleDAO.findSchedulesByCustomer(customer);
        assertEquals(2, schedules.size());

        RecurringSchedule s1 = schedules.get(0);
        assertEquals(waste, s1.getWaste());
        assertEquals(Frequency.WEEKLY, s1.getFrequency());
        assertNotNull(s1.getNextCollectionDate());

        RecurringSchedule s2 = schedules.get(1);
        assertEquals(waste, s2.getWaste());
        assertEquals(Frequency.MONTHLY, s2.getFrequency());
        assertNotNull(s2.getNextCollectionDate());
    }

    @Test
    void testCalculateNextDate_FirstCollection() {
        LocalDate startDate = LocalDate.of(2025, 4, 24);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste,
                startDate, Frequency.WEEKLY);

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_MonthlyCollection() {
        LocalDate startDate = LocalDate.of(2025, 4, 24);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.MONTHLY);

        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 28));

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 6, 2);
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_InThePast() {
        DateUtils mockDateUtils = new DateUtils() {
            @Override
            public LocalDate getCurrentDate() {
                return LocalDate.of(2025, 5, 1);
            }
        };

        recurringScheduleManager.setDateUtils(mockDateUtils);

        LocalDate startDate = LocalDate.of(2025, 4, 1);

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);

        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 10));

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        LocalDate expectedDate = LocalDate.of(2025, 5, 5);

        assertTrue(nextDate.isAfter(LocalDate.of(2025, 5, 1)));
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testAlignToScheduledDay() {
        LocalDate startDate = LocalDate.of(2025, 4, 25);
        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);
        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);

        schedule.setStartDate(LocalDate.of(2025, 4, 30));
        schedule.setNextCollectionDate(null);

        nextDate = recurringScheduleManager.calculateNextDate(schedule);
        expectedDate = LocalDate.of(2025, 5, 5);

        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testUpdateNextDates() {
        LocalDate today = dateUtils.getCurrentDate();
        LocalDate startDate = today;
        LocalDate oldNextDate = today.minusDays(2);

        wasteDAO.insert(waste);
        wasteScheduleDAO.insert(new WasteSchedule(waste, DayOfWeek.MONDAY));

        RecurringSchedule schedule = new RecurringSchedule(customer, waste, startDate, Frequency.WEEKLY);
        schedule.setNextCollectionDate(oldNextDate);
        schedule.setScheduleStatus(RecurringSchedule.ScheduleStatus.ACTIVE);

        ValidateUtils.validateEntity(schedule);
        recurringScheduleDAO.insert(schedule);

        recurringScheduleManager.updateNextDates();

        RecurringSchedule updated = recurringScheduleDAO.findSchedulesByCustomer(customer).get(0);
        assertTrue(updated.getNextCollectionDate().isAfter(oldNextDate));
    }
}
