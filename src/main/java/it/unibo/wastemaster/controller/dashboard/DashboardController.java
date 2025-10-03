package it.unibo.wastemaster.controller.dashboard;

import it.unibo.wastemaster.domain.service.*;
import it.unibo.wastemaster.domain.model.*;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Sets the collection manager used for statistics and charts.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the invoice manager used for statistics.
     *
     * @param invoiceManager the InvoiceManager to use
     */
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    /**
     * Sets the trip manager used for statistics.
     *
     * @param tripManager the TripManager to use
     */
    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    /**
     * Sets the notification manager used for displaying recent notifications.
     *
     * @param notificationManager the NotificationManager to use
     */
    public void setNotificationManager(NotificationManager notificationManager) {
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
     * Updates the total statistics labels for customers, collections, trips, and invoices to pay.
     */
    private void updateTotals() {
        if (customerManager != null)
            totalCustomersLabel.setText(String.valueOf(customerManager.getAllCustomers().size()));
        if (collectionManager != null)
            totalCollectionsLabel.setText(String.valueOf(collectionManager.getAllCollections().size()));
        if (tripManager != null)
            totalTripsLabel.setText(String.valueOf(tripManager.countCompletedTrips()));
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
        if (collectionsChart == null || collectionManager == null)
            return;

        collectionsChart.getData().clear();

        XYChart.Series<String, Number> cancelledSeries = new XYChart.Series<>();
        cancelledSeries.setName("Cancelled");
        XYChart.Series<String, Number> toPaySeries = new XYChart.Series<>();
        toPaySeries.setName("To Pay");
        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Completed");

        List<String> monthOrder = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                "Dec");

        for (String month : monthOrder) {
            long cancelled = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.CANCELLED)
                    .count();
            long toPay = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.ACTIVE)
                    .count();
            long completed = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.COMPLETED)
                    .count();

            cancelledSeries.getData().add(new XYChart.Data<>(month, cancelled));
            toPaySeries.getData().add(new XYChart.Data<>(month, toPay));
            completedSeries.getData().add(new XYChart.Data<>(month, completed));
        }

        collectionsChart.getData().addAll(cancelledSeries, toPaySeries, completedSeries);
    }

    /**
     * Updates the notifications list with the most recent events.
     */
    private void updateNotifications() {
        notificationsList.getItems().clear();

        if (notificationManager != null) {
            DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm");
            List<Notification> lastEvents = notificationManager.getLast5Events();
            List<String> displayNotifications = lastEvents.stream()
                    .map(n -> String.format("%s - %s", n.getMessage(), n.getTimestamp().format(TS_FMT)))
                    .toList();
            notificationsList.setItems(FXCollections.observableArrayList(displayNotifications));
        }
    }
}
