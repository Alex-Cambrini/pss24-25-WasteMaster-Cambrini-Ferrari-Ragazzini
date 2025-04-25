package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste.WasteType;
import it.unibo.wastemaster.core.utils.DateUtils;
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
        waste = new Waste(Waste.WasteType.GLASS, true, false);
        wasteSchedule = new WasteSchedule(waste, DayOfWeek.MONDAY);
        customerDAO.insert(customer);
        wasteDAO.insert(waste);
        wasteScheduleDAO.insert(wasteSchedule);
    }

    @Test
    void testCreateRecurringSchedule() {
        recurringScheduleManager.createRecurringSchedule(customer, WasteType.GLASS, dateUtils.getCurrentDate(),
                Frequency.WEEKLY);
        recurringScheduleManager.createRecurringSchedule(customer, WasteType.GLASS, dateUtils.getCurrentDate(),
                Frequency.MONTHLY);

        List<RecurringSchedule> schedules = recurringScheduleDAO.findSchedulesByCustomer(customer);
        assertEquals(2, schedules.size());

        RecurringSchedule s1 = schedules.get(0);
        assertEquals(WasteType.GLASS, s1.getWasteType());
        assertEquals(Frequency.WEEKLY, s1.getFrequency());
        assertNotNull(s1.getNextCollectionDate());

        RecurringSchedule s2 = schedules.get(1);
        assertEquals(WasteType.GLASS, s2.getWasteType());
        assertEquals(Frequency.MONTHLY, s2.getFrequency());
        assertNotNull(s2.getNextCollectionDate());
    }

    @Test
    void testCalculateNextDate_FirstCollection() {
        // Impostiamo la data di inizio
        LocalDate startDate = LocalDate.of(2025, 4, 24); // Ad esempio, 24 aprile 2025

        // Creiamo una nuova pianificazione ricorrente con frequenza settimanale
        RecurringSchedule schedule = new RecurringSchedule(customer, WasteType.GLASS,
                startDate, Frequency.WEEKLY);

        // Calcoliamo la data del prossimo ritiro
        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        // La data successiva dovrebbe essere 2 giorni dopo la startDate (27 aprile
        // 2025, poiché il giorno di raccolta è lunedì)
        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_MonthlyCollection() {
        // Data di inizio
        LocalDate startDate = LocalDate.of(2025, 4, 24);

        // Creiamo una pianificazione mensile
        RecurringSchedule schedule = new RecurringSchedule(customer, WasteType.GLASS, startDate, Frequency.MONTHLY);

        // Impostiamo la data del primo ritiro (28 aprile 2025, lunedì)
        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 28));

        // La data del prossimo ritiro dovrebbe essere 1 mese dopo, ovvero il lunedì
        // successivo al 28 maggio 2025
        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        // La data prevista sarà 2 giugno 2025 (lunedì)
        LocalDate expectedDate = LocalDate.of(2025, 6, 2); // 2 giugno 2025, lunedì
        assertEquals(expectedDate, nextDate);
    }

    @Test
    void testCalculateNextDate_InThePast() {
        // === MOCK DI DateUtils per restituire una data finta ===
        DateUtils mockDateUtils = new DateUtils() {
            @Override
            public LocalDate getCurrentDate() {
                return LocalDate.of(2025, 5, 1); // Data "finta" che simula "oggi"
            }
        };

        recurringScheduleManager.setDateUtils(mockDateUtils);

        LocalDate startDate = LocalDate.of(2025, 4, 1); // 1 aprile 2025

        // Creiamo una pianificazione settimanale
        RecurringSchedule schedule = new RecurringSchedule(customer, WasteType.GLASS, startDate, Frequency.WEEKLY);

        // Simuliamo una data successiva nel passato
        schedule.setNextCollectionDate(LocalDate.of(2025, 4, 10));

        // Impostiamo la data di oggi come l'1 maggio 2025 ( non serve )
        

        // La data del prossimo ritiro dovrebbe essere dopo oggi, allineata al lunedì
        // successivo
        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);

        // La data attesa è il prossimo lunedì, ovvero il 5 maggio 2025
        LocalDate expectedDate = LocalDate.of(2025, 5, 5); // 5 maggio, primo lunedì dopo il 1 maggio

        assertTrue(nextDate.isAfter(LocalDate.of(2025, 5, 1))); // Assicuriamoci che la data calcolata sia nel futuro
        assertEquals(expectedDate, nextDate); // Assicuriamoci che la data corrisponda al prossimo lunedì
    }

    @Test
    void testAlignToScheduledDay() {
        LocalDate startDate = LocalDate.of(2025, 4, 25);
        RecurringSchedule schedule = new RecurringSchedule(customer, WasteType.GLASS, startDate, Frequency.WEEKLY);

        LocalDate nextDate = recurringScheduleManager.calculateNextDate(schedule);
        LocalDate expectedDate = LocalDate.of(2025, 4, 28);
        assertEquals(expectedDate, nextDate);

        schedule.setStartDate(LocalDate.of(2025, 4, 30));
        schedule.setNextCollectionDate(null);

        nextDate = recurringScheduleManager.calculateNextDate(schedule);
        expectedDate = LocalDate.of(2025, 5, 5);

        assertEquals(expectedDate, nextDate);
    }
}
