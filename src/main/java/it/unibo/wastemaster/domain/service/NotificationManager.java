package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.model.Notification;
import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {


    private final TripRepository tripRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    private NotificationManager(TripRepository tripRepository,
                                InvoiceRepository invoiceRepository,
                                CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        }


    public List<Notification> getLast5Events() {
        List<Notification> events = new ArrayList<>();

        customerRepository.findLast5Inserted()
                .forEach(c -> events.add(new Notification("New Customer: " + c.toString(), c.getCreatedDate())));

        invoiceRepository.findLast5InvoicesEvent()
                .forEach(i -> {
                    LocalDateTime date = i.getPaymentDate() != null ? i.getPaymentDate() : i.getIssueDate();
                    events.add(new Notification("Invoice Event: " + i.toString(), date));
                });

        tripRepository.findLast5Inserted()
                .forEach(t -> events.add(new Notification("Trip Event: " + t.toString(), t.getDepartureTime())));

        events.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

        return events.stream().limit(5).toList();
    }
}

