package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class InvoiceManagerTest extends AbstractDatabaseTest {

    private static final int FUTURE_TEST_YEAR = LocalDate.now().getYear() + 1;
    private static final double TEST_AMOUNT = 30.0;
    private static final double DELTA = 0.001;
    private static final int MARCH = 3;
    private static final int SEPTEMBER = 9;
    private static final int OCTOBER = 10;
    private static final int MARCH_DAY = 15;
    private static final int SEPTEMBER_DAY = 10;
    private static final int OCTOBER_DAY = 5;
    private static final int FUTURE_DAY = 20;

    private Customer customer;
    private Waste waste;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        customer = new Customer("Mario", "Rossi",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "mario.rossi@example.com", "1234567890");
        waste = new Waste("Organico", true, false);

        getCustomerDAO().insert(customer);
        getWasteDAO().insert(waste);
    }

    private Collection insertCompletedCollection(
    final LocalDate collectionScheduledDate) {
        OneTimeSchedule schedule = new OneTimeSchedule(customer, waste,
                LocalDate.now().plusDays(1));
        getOneTimeScheduleDAO().insert(schedule);

        Collection collection = new Collection(schedule);
        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
        collection.setCollectionDate(collectionScheduledDate); 

        getCollectionDAO().insert(collection);

        return collection;
    }

    @Test
    void testGenerateInvoicesForFirstHalf() {
        int year = FUTURE_TEST_YEAR;

        insertCompletedCollection(LocalDate.of(year, MARCH, MARCH_DAY));
        insertCompletedCollection(LocalDate.of(year, SEPTEMBER, SEPTEMBER_DAY));

        List<Invoice> invoices = getInvoiceManager().generateInvoicesForFirstHalf(year);

        assertEquals(1, invoices.size(), 
            "Exactly 1 invoice should be generated for the first half.");
        assertNotNull(invoices.get(0));
        assertEquals(TEST_AMOUNT, invoices.get(0).getAmount(), DELTA);
        assertEquals(Invoice.PaymentStatus.UNPAID, invoices.get(0).getPaymentStatus());
    }

    @Test
    void testGenerateInvoicesForSecondHalf() {
        int year = FUTURE_TEST_YEAR;

        insertCompletedCollection(LocalDate.of(year, OCTOBER, OCTOBER_DAY));
        insertCompletedCollection(LocalDate.of(year, MARCH, FUTURE_DAY));

        List<Invoice> invoices = getInvoiceManager().generateInvoicesForSecondHalf(year);
        assertEquals(1, invoices.size(), 
            "Exactly 1 invoice should be generated for the second half.");
        assertNotNull(invoices.get(0));
        assertEquals(TEST_AMOUNT, invoices.get(0).getAmount(), DELTA);
        assertEquals(Invoice.PaymentStatus.UNPAID, invoices.get(0).getPaymentStatus());
    }

    @Test
    void testNoInvoicesForEmptyPeriod() {
        int yearForEmptyPeriod = FUTURE_TEST_YEAR + 1;
        List<Invoice> invoices = getInvoiceManager().
                                 generateInvoicesForFirstHalf(yearForEmptyPeriod);
        assertTrue(invoices.isEmpty(), 
            "There should be no invoices for a period with no collections.");
    }
}
