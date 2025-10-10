package it.unibo.wastemaster.controller.dashboard;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Notification;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.NotificationManager;
import it.unibo.wastemaster.domain.service.TripManager;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller for the main dashboard view, displaying overall statistics,
 * recent notifications, and monthly collection charts.
 */
public class DashboardController {

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label totalCollectionsLabel;

    @FXML
    private Label totalTripsLabel;

    @FXML
    private Label invoicesToPayLabel;

    @FXML
    private ListView<String> notificationsList;

    @FXML
    private StackedBarChart<String, Number> collectionsChart;

    private CustomerManager customerManager;
    private CollectionManager collectionManager;
    private InvoiceManager invoiceManager;
    private TripManager tripManager;
    private NotificationManager notificationManager;

    /**
     * Sets the customer manager used for statistics.
     *
     * @param customerManager the CustomerManager to use
     */
    public void setCustomerManager(final CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Sets the collection manager used for statistics and charts.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(final CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the invoice manager used for statistics.
     *
     * @param invoiceManager the InvoiceManager to use
     */
    public void setInvoiceManager(final InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    /**
     * Sets the trip manager used for statistics.
     *
     * @param tripManager the TripManager to use
     */
    public void setTripManager(final TripManager tripManager) {
        this.tripManager = tripManager;
    }

    /**
     * Sets the notification manager used for displaying recent notifications.
     *
     * @param notificationManager the NotificationManager to use
     */
    public void setNotificationManager(final NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    /**
     * Initializes the dashboard view.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Loads and updates all dashboard statistics, charts, and notifications.
     */
    public void initData() {
        updateTotals();
        updateCollectionsChart();
        updateNotifications();
    }

    /**
     * Updates the total statistics labels for customers, collections, trips, and
     * invoices to pay.
     */
    private void updateTotals() {
        if (customerManager != null) {
            totalCustomersLabel.setText(
                    String.valueOf(customerManager.getAllCustomers().size()));
        }
        if (collectionManager != null) {
            totalCollectionsLabel.setText(
                    String.valueOf(collectionManager.getAllCollections().size()));
        }
        if (tripManager != null) {
            totalTripsLabel.setText(String.valueOf(tripManager.countCompletedTrips()));
        }
        if (invoiceManager != null) {
            long toPay = invoiceManager.getAllInvoices().stream()
                    .filter(inv -> inv.getPaymentStatus() != Invoice.PaymentStatus.PAID)
                    .count();
            invoicesToPayLabel.setText(String.valueOf(toPay));
        }
    }

    /**
     * Updates the monthly collections chart with cancelled, to pay, and completed series.
     */
    private void updateCollectionsChart() {
        if (collectionsChart == null || collectionManager == null) {
            return;
        }

        collectionsChart.getData().clear();

        XYChart.Series<String, Number> cancelledSeries = new XYChart.Series<>();
        cancelledSeries.setName("Cancelled");
        XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
        activeSeries.setName("Active");
        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Completed");

        List<String> monthOrder =
                List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                        "Oct", "Nov",
                        "Dec");

        for (String month : monthOrder) {
            long cancelled = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus()
                            == Collection.CollectionStatus.CANCELLED)
                    .count();
            long active = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus()
                            == Collection.CollectionStatus.ACTIVE)
                    .count();
            long completed = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus()
                            == Collection.CollectionStatus.COMPLETED)
                    .count();

            cancelledSeries.getData().add(new XYChart.Data<>(month, cancelled));
            activeSeries.getData().add(new XYChart.Data<>(month, active));
            completedSeries.getData().add(new XYChart.Data<>(month, completed));
        }

        collectionsChart.getData().addAll(cancelledSeries, activeSeries, completedSeries);
    }

    /**
     * Updates the notifications list with the most recent events.
     */
    private void updateNotifications() {
        notificationsList.getItems().clear();

        if (notificationManager != null) {
            DateTimeFormatter tsFmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm");
            List<Notification> lastEvents = notificationManager.getLast5Events();
            List<String> displayNotifications = lastEvents.stream()
                    .map(n -> String.format("%s - %s", n.getMessage(),
                            n.getTimestamp().format(tsFmt)))
                    .toList();
            notificationsList.setItems(
                    FXCollections.observableArrayList(displayNotifications));
        }
    }
}
