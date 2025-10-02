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

/**
 * Service class responsible for managing notifications related to system events
 * such as customer registrations, invoice issuances, and trip updates.
 */
public class NotificationManager {

        private final TripRepository tripRepository;
        private final InvoiceRepository invoiceRepository;
        private final CustomerRepository customerRepository;

        /**
         * Constructs a NotificationManager with the given repositories.
         *
         * @param tripRepository     repository for accessing trip data
         * @param invoiceRepository  repository for accessing invoice data
         * @param customerRepository repository for accessing customer data
         */
        public NotificationManager(TripRepository tripRepository,
                        InvoiceRepository invoiceRepository,
                        CustomerRepository customerRepository) {
                this.tripRepository = tripRepository;
                this.invoiceRepository = invoiceRepository;
                this.customerRepository = customerRepository;
        }

        /**
         * Formats a numeric amount as a currency string in EUR using Italian locale.
         *
         * @param amount the amount to format
         * @return the formatted currency string
         */
        private static String formatEur(double amount) {
                NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.ITALY);
                nf.setCurrency(Currency.getInstance("EUR"));
                return nf.format(amount);
        }

        /**
         * Retrieves the 5 most recent system events across customers, invoices, and
         * trips.
         * Events are aggregated, sorted by timestamp (most recent first),
         * and converted into {@link Notification} objects.
         *
         * @return a list of up to 5 latest notifications
         */
        public List<Notification> getLast5Events() {
                List<Notification> events = new ArrayList<>();

                customerRepository.findLast5Inserted()
                                .forEach(c -> events.add(new Notification(
                                                String.format("New customer: %s %s (%s)",
                                                                c.getName(), c.getSurname(), c.getEmail()),
                                                c.getCreatedDate())));

                invoiceRepository.findLast5InvoicesEvent()
                                .forEach(i -> {
                                        LocalDateTime date = i.getPaymentDate() != null ? i.getPaymentDate()
                                                        : i.getIssueDate();
                                        String customerName = (i.getCustomer() != null)
                                                        ? (i.getCustomer().getName() + " "
                                                                        + i.getCustomer().getSurname())
                                                        : "Unknown customer";
                                        String statusText = (i.getPaymentStatus() == Invoice.PaymentStatus.PAID)
                                                        ? "Paid"
                                                        : "Not Paid";
                                        events.add(new Notification(
                                                        String.format("Invoice #%d for %s - %s: %s",
                                                                        i.getInvoiceId(), customerName, statusText,
                                                                        formatEur(i.getAmount())),
                                                        date));
                                });

                tripRepository.findLast5Modified()
                                .forEach(t -> {
                                        String vehicle = t.getAssignedVehicle() != null
                                                        ? t.getAssignedVehicle().getPlate()
                                                        : "N/A";
                                        String status = (t.getStatus() != null) ? t.getStatus().name() : "UNKNOWN";
                                        events.add(new Notification(
                                                        String.format("Trip #%d - Vehicle: %s - Status: %s",
                                                                        t.getTripId(), vehicle, status),
                                                        t.getDepartureTime()));
                                });

                events.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
                return events.stream().limit(5).toList();
        }
}
