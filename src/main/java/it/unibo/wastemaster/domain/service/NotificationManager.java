package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Notification;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class NotificationManager {

    private final TripRepository tripRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public NotificationManager(TripRepository tripRepository,
            InvoiceRepository invoiceRepository,
            CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    private static String formatEur(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.ITALY);
        nf.setCurrency(Currency.getInstance("EUR"));
        return nf.format(amount);
    }

    public List<Notification> getLast5Events() {
        List<Notification> events = new ArrayList<>();

        customerRepository.findLast5Inserted()
                .forEach(c -> events.add(new Notification(
                        "New customer: " + c.getName() + " " + c.getSurname(),
                        c.getCreatedDate())));

        invoiceRepository.findLast5InvoicesEvent()
                .forEach(i -> events.add(new Notification(
                        "Invoice #" + i.getInvoiceId() + " for " + i.getCustomer().getName() + " " + i.getCustomer().getSurname()
                                + " - " + i.getPaymentStatus() + ": " + formatEur(i.getAmount()),
                        i.getLastModified())));

        tripRepository.findLast5Modified()
                .forEach(t -> events.add(new Notification(
                        "Trip #" + t.getTripId() + " - Vehicle: " + t.getAssignedVehicle().getPlate(),
                        t.getLastModified())));

        events.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
        return events.stream().limit(5).toList();
    }
}
