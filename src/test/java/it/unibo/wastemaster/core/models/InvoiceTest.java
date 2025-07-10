package it.unibo.wastemaster.core.models;

import static org.junit.jupiter.api.Assertions.*;

import it.unibo.wastemaster.core.AbstractDatabaseTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvoiceTest extends AbstractDatabaseTest {

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
        OneTimeSchedule schedule = new OneTimeSchedule(customer, waste, LocalDate.now().plusDays(1));
        collection = new Collection(schedule);

        getCustomerDAO().insert(customer);
        getWasteDAO().insert(waste);
        getOneTimeScheduleDAO().insert(schedule);
        getCollectionDAO().insert(collection);

        invoice = new Invoice(collection);
        invoice.setAmount(100.0);
        invoice.setIssueDate(LocalDate.now());
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
    }

   
}