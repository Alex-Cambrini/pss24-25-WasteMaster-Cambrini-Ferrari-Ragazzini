package it.unibo.wastemaster.core.services;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.core.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceManagerTest extends AbstractDatabaseTest {

    private Customer customer;
    private Waste waste;

    
    private static final int FUTURE_TEST_YEAR = LocalDate.now().getYear() + 1; 

    
    private static final LocalDate FUTURE_DATE_FOR_SCHEDULE_VALIDATION = LocalDate.of(2030, 1, 1);

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

    private Collection insertCompletedCollection(LocalDate collectionScheduledDate) {

        OneTimeSchedule schedule = new OneTimeSchedule(customer, waste, FUTURE_DATE_FOR_SCHEDULE_VALIDATION);
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
        
        insertCompletedCollection(LocalDate.of(year, 3, 15));
        
        
        insertCompletedCollection(LocalDate.of(year, 9, 10));

        List<Invoice> invoices = getInvoiceManager().generateInvoicesForFirstHalf(year);
        
        assertEquals(1, invoices.size(), "Exactly 1 invoice should be generated for the first half of the future year.");        assertNotNull(invoices.get(0));
        assertEquals(30.0, invoices.get(0).getAmount(), 0.001);
        assertEquals(Invoice.PaymentStatus.UNPAID, invoices.get(0).getPaymentStatus());
    }
   

    // @Test
    // void testGenerateInvoicesForSecondHalf() {
    //     int year = FUTURE_TEST_YEAR;
        
    //     insertCompletedCollection(LocalDate.of(year, 10, 5));
        
    //     insertCompletedCollection(LocalDate.of(year, 3, 20));

    //     List<Invoice> invoices = getInvoiceManager().generateInvoicesForSecondHalf(year);
    //     assertEquals(1, invoices.size(), "Dovrebbe essere generata esattamente 1 fattura per il secondo semestre dell'anno futuro.");
    //     assertNotNull(invoices.get(0));
    //     assertEquals(30.0, invoices.get(0).getAmount(), 0.001);
    //     assertEquals(Invoice.PaymentStatus.UNPAID, invoices.get(0).getPaymentStatus());
    // }



    @Test
    void testNoInvoicesForEmptyPeriod() {
        int yearForEmptyPeriod = FUTURE_TEST_YEAR + 1; 
        List<Invoice> invoices = getInvoiceManager().generateInvoicesForFirstHalf(yearForEmptyPeriod);
        assertTrue(invoices.isEmpty(), "There should be no invoices for a period with no collections.");  
    }

}