package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.model.Collection.CollectionStatus;
import java.time.LocalDate;
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

        invoice = new Invoice(collection);
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
        assertEquals(collection, invoice.getCollection());
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
        getEntityManager().getTransaction().begin();
        getInvoiceDAO().insert(invoice);
        getEntityManager().getTransaction().commit();

        Invoice found = getInvoiceDAO().findById(invoice.getInvoiceId());
        assertNotNull(found);
        assertEquals(invoice.getAmount(), found.getAmount());
        assertEquals(invoice.getCollection().getCollectionId(),
            found.getCollection().getCollectionId());

        int foundId = found.getInvoiceId();
        getEntityManager().getTransaction().begin();
        getInvoiceDAO().delete(found);
        getEntityManager().getTransaction().commit();

        Invoice deleted = getInvoiceDAO().findById(foundId);
        assertNull(deleted);
    }
}
