package it.unibo.wastemaster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceTest extends AbstractDatabaseTest {

    private static final double TEST_AMOUNT = 200.0;

    private Invoice invoice;
    private Collection collection;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        Customer customer = new Customer(
                "Mario",
                "Rossi",
                new Location("Via Roma", "10", "Bologna", "40100"),
                "mario.rossi@example.com",
                "1234567890"
        );
        Waste waste = new Waste("Organico", true, false);
        OneTimeSchedule schedule = new OneTimeSchedule(customer,
                waste,
                LocalDate.now().plusDays(1));
        collection = new Collection(schedule);

        getCustomerDAO().insert(customer);
        getWasteDAO().insert(waste);
        getOneTimeScheduleDAO().insert(schedule);
        getCollectionDAO().insert(collection);
        collection.setCollectionStatus(CollectionStatus.COMPLETED);
        getCollectionDAO().update(collection);

        LocalDateTime now = LocalDateTime.now();
        invoice = new Invoice(customer, List.of(collection), 50.0, 150.0, 2, 3, now);
        invoice.setAmount(100.0);
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
    }

    @Test
    void testGetterSetter() {
        invoice.setAmount(TEST_AMOUNT);
        invoice.setPaymentStatus(Invoice.PaymentStatus.UNPAID);
        invoice.setTotalRecurring(50.0);
        invoice.setTotalOnetime(150.0);
        invoice.setRecurringCount(2);
        invoice.setOnetimeCount(3);
        LocalDateTime now = LocalDateTime.now();
        invoice.setLastModified(now);
        invoice.setPaymentDate(now.plusDays(1));

        assertEquals(TEST_AMOUNT, invoice.getAmount());
        assertEquals(Invoice.PaymentStatus.UNPAID, invoice.getPaymentStatus());
        assertEquals(50.0, invoice.getTotalRecurring());
        assertEquals(150.0, invoice.getTotalOnetime());
        assertEquals(2, invoice.getRecurringCount());
        assertEquals(3, invoice.getOnetimeCount());
        assertEquals(now, invoice.getLastModified());
        assertEquals(now.plusDays(1), invoice.getPaymentDate());
        assertTrue(invoice.getCollections().contains(collection));
    }

    @Test
    void testToString() {
        String output = invoice.toString();
        assertNotNull(output);
        assertTrue(output.contains("Invoice"));
        assertTrue(output.contains(String.format("%.2f", invoice.getAmount())));
        assertTrue(output.contains(invoice.getPaymentStatus().name()));
    }

    @Test
    void testPersistence() {
        getInvoiceDAO().insert(invoice);
        Optional<Invoice> foundOpt = getInvoiceDAO().findById(invoice.getInvoiceId());
        assertTrue(foundOpt.isPresent());

        Invoice found = foundOpt.get();
        assertEquals(invoice.getAmount(), found.getAmount());
        assertEquals(invoice.getTotalRecurring(), found.getTotalRecurring());
        assertEquals(invoice.getTotalOnetime(), found.getTotalOnetime());
        assertEquals(invoice.getRecurringCount(), found.getRecurringCount());
        assertEquals(invoice.getOnetimeCount(), found.getOnetimeCount());
        assertEquals(invoice.getLastModified(), found.getLastModified());
        assertEquals(invoice.getPaymentDate(), found.getPaymentDate());
        assertEquals(invoice.isDeleted(), found.isDeleted());
        assertTrue(found.getCollections().stream()
                .anyMatch(c -> c.getCollectionId() == collection.getCollectionId()));

        int foundId = found.getInvoiceId();
        getInvoiceDAO().delete(found);
        getEntityManager().getTransaction().commit();

        Optional<Invoice> deletedOpt = getInvoiceDAO().findById(foundId);
        assertTrue(deletedOpt.isEmpty());
    }

    @Test
    void testIsDeletedSetterGetter() {
        assertFalse(invoice.isDeleted());
        invoice.setDeleted(true);
        assertTrue(invoice.isDeleted());
        invoice.setDeleted(false);
        assertFalse(invoice.isDeleted());
    }
}
