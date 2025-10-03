package it.unibo.wastemaster.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Invoice.PaymentStatus;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.model.OneTimeSchedule;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.infrastructure.AbstractDatabaseTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceManagerTest extends AbstractDatabaseTest {

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
        OneTimeSchedule schedule =
                new OneTimeSchedule(customer, waste, collectionScheduledDate);
        getOneTimeScheduleDAO().insert(schedule);

        Collection collection = new Collection(schedule);
        collection.setCollectionStatus(Collection.CollectionStatus.COMPLETED);
        collection.setCollectionDate(collectionScheduledDate);

        getCollectionDAO().insert(collection);

        return collection;
    }

    @Test
    void testGetTotalBilledAmountForCustomer() {
        Collection c1 = insertCompletedCollection(LocalDate.now());
        Collection c2 = insertCompletedCollection(LocalDate.now().plusDays(1));

        Invoice invoice1 =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c1)));
        invoice1.setAmount(50.0);
        invoice1.setPaymentStatus(PaymentStatus.UNPAID);
        getInvoiceDAO().update(invoice1);

        Invoice invoice2 =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c2)));
        invoice2.setAmount(100.0);
        invoice2.setPaymentStatus(PaymentStatus.PAID);
        getInvoiceDAO().update(invoice2);

        double total = getInvoiceManager().getTotalBilledAmountForCustomer(customer);
        assertEquals(150.0, total, 0.001);
    }

    @Test
    void testGetTotalPaidAmountForCustomer() {
        Collection c1 = insertCompletedCollection(LocalDate.now());
        Collection c2 = insertCompletedCollection(LocalDate.now().plusDays(1));

        Invoice invoice1 =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c1)));
        invoice1.setAmount(50.0);
        invoice1.setPaymentStatus(PaymentStatus.UNPAID);
        getInvoiceDAO().update(invoice1);

        Invoice invoice2 =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c2)));
        invoice2.setAmount(100.0);
        invoice2.setPaymentStatus(PaymentStatus.PAID);
        getInvoiceDAO().update(invoice2);

        double totalPaid = getInvoiceManager().getTotalPaidAmountForCustomer(customer);
        assertEquals(100.0, totalPaid, 0.001);
    }

    @Test
    void testDeleteInvoiceSetsDeletedAndUnbillsCollections() {
        Collection c1 = insertCompletedCollection(LocalDate.now());
        Invoice invoice =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c1)));
        int invoiceId = invoice.getInvoiceId();

        boolean canceled = getInvoiceManager().deleteInvoice(invoiceId);
        assertTrue(canceled);

        Invoice canceledInvoice = getInvoiceDAO().findById(invoiceId).get();
        assertTrue(canceledInvoice.isDeleted());
        for (Collection c : canceledInvoice.getCollections()) {
            assertFalse(c.getIsBilled());
        }
    }

    @Test
    void testCannotModifyCanceledInvoice() {
        Collection c1 = insertCompletedCollection(LocalDate.now());
        Invoice invoice =
                getInvoiceManager().createInvoice(customer, new ArrayList<>(List.of(c1)));
        int invoiceId = invoice.getInvoiceId();

        getInvoiceManager().deleteInvoice(invoiceId);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                getInvoiceManager().markInvoiceAsPaid(invoiceId)
        );
        assertEquals("Cannot modify a deleted invoice.", ex.getMessage());
    }
}
