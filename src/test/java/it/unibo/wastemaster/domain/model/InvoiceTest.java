package it.unibo.wastemaster.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
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
                "Mario", // nome
                "Rossi", // cognome
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

        invoice = new Invoice(customer, List.of(collection), 0, 0, 0, 0, null);
        invoice.setAmount(100.0);
        invoice.setIssueDate(LocalDate.now());
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
    }

    @Test
    void testGetterSetter() {
        invoice.setAmount(TEST_AMOUNT);
        invoice.setPaymentStatus(Invoice.PaymentStatus.UNPAID);
        assertEquals(TEST_AMOUNT, invoice.getAmount());
        assertEquals(Invoice.PaymentStatus.UNPAID, invoice.getPaymentStatus());
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
        assertTrue(found.getCollections().stream()
        .anyMatch(c -> c.getCollectionId() == collection.getCollectionId()));

        int foundId = found.getInvoiceId();

        getInvoiceDAO().delete(found);
        getEntityManager().getTransaction().commit();

        Optional<Invoice> deletedOpt = getInvoiceDAO().findById(foundId);
        assertTrue(deletedOpt.isEmpty());
    }
}