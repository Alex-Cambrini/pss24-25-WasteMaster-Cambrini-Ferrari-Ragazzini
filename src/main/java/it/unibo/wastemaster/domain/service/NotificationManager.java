package it.unibo.wastemaster.domain.service;

import it.unibo.wastemaster.domain.repository.CustomerRepository;
import it.unibo.wastemaster.domain.repository.InvoiceRepository;
import it.unibo.wastemaster.domain.repository.TripRepository;

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
}
