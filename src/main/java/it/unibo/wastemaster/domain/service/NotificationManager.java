package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Notification;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;
import it.unibo.wastemaster.domain.model.Invoice;

import java.text.NumberFormat;
import java.time.LocalDateTime;
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
                        String.format("New customer: %s %s (%s)",
                                c.getName(), c.getSurname(), c.getEmail()),
                        c.getCreatedDate())));

        invoiceRepository.findLast5InvoicesEvent()
                .forEach(i -> {
                    LocalDateTime date = i.getPaymentDate() != null ? i.getPaymentDate() : i.getIssueDate();
                    String customerName = (i.getCustomer() != null)
                            ? (i.getCustomer().getName() + " " + i.getCustomer().getSurname())
                            : "Unknown customer";
                    String statusText = (i.getPaymentStatus() == Invoice.PaymentStatus.PAID) ? "Paid" : "Not Paid";
                    events.add(new Notification(
                            String.format("Invoice #%d for %s - %s: %s",
                                    i.getInvoiceId(), customerName, statusText, formatEur(i.getAmount())),
                            date));
                });

        tripRepository.findLast5Inserted()
                .forEach(t -> events.add(new Notification(
                        String.format("Trip #%d - Vehicle: %s",
                                t.getTripId(),
                                t.getAssignedVehicle() != null ? t.getAssignedVehicle().getPlate() : "N/A"),
                        t.getDepartureTime()
                )));

        events.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
        return events.stream().limit(5).toList();
    }
}
