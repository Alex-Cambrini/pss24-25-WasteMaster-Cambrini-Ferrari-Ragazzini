package it.unibo.wastemaster.controller.dashboard;

import it.unibo.wastemaster.domain.service.*;
import it.unibo.wastemaster.domain.model.*;
import javafx.fxml.FXML;import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML private Label totalCustomersLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label totalTripsLabel;
    @FXML private Label invoicesToPayLabel;
    @FXML private ListView<String> notificationsList;
    @FXML private StackedBarChart<String, Number> collectionsChart;

    private CustomerManager customerManager;
    private CollectionManager collectionManager;
    private InvoiceManager invoiceManager;
    private TripManager tripManager;
    private NotificationManager notificationManager;

    // --- Setters ---
    public void setCustomerManager(CustomerManager customerManager) { this.customerManager = customerManager; }
    public void setCollectionManager(CollectionManager collectionManager) { this.collectionManager = collectionManager; }
    public void setInvoiceManager(InvoiceManager invoiceManager) { this.invoiceManager = invoiceManager; }
    public void setTripManager(TripManager tripManager) { this.tripManager = tripManager; }
    public void setNotificationManager(NotificationManager notificationManager) { this.notificationManager = notificationManager; }


    @FXML
    public void initialize() { }

    // --- Main dashboard update ---
    public void initData() {
        updateTotals();
        updateCollectionsChart();
        updateNotifications();
    }

    // --- Totals ---
    private void updateTotals() {
        if (customerManager != null)
            totalCustomersLabel.setText(String.valueOf(customerManager.getAllCustomers().size()));
        if (collectionManager != null)
            totalCollectionsLabel.setText(String.valueOf(collectionManager.getAllCollections().size()));
        if (invoiceManager != null) {
            long toPay = invoiceManager.getAllInvoices().stream()
                    .filter(inv -> inv.getPaymentStatus() != Invoice.PaymentStatus.PAID)
                    .count();
            invoicesToPayLabel.setText(String.valueOf(toPay));
        }
    }

    // --- Collections chart ---
    private void updateCollectionsChart() {
        if (collectionsChart == null || collectionManager == null) return;

        collectionsChart.getData().clear();

        XYChart.Series<String, Number> cancelledSeries = new XYChart.Series<>();
        cancelledSeries.setName("Cancelled");
        XYChart.Series<String, Number> toPaySeries = new XYChart.Series<>();
        toPaySeries.setName("To Pay");
        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Completed");

        List<String> monthOrder = List.of("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");

        for (String month : monthOrder) {
            long cancelled = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.CANCELLED)
                    .count();
            long toPay = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.ACTIVE)
                    .count();
            long completed = collectionManager.getAllCollections().stream()
                    .filter(c -> c.getCollectionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).equals(month))
                    .filter(c -> c.getCollectionStatus() == Collection.CollectionStatus.COMPLETED)
                    .count();

            cancelledSeries.getData().add(new XYChart.Data<>(month, cancelled));
            toPaySeries.getData().add(new XYChart.Data<>(month, toPay));
            completedSeries.getData().add(new XYChart.Data<>(month, completed));
        }

        collectionsChart.getData().addAll(cancelledSeries, toPaySeries, completedSeries);
    }

    // --- Notifications ---
    private void updateNotifications() {
        notificationsList.getItems().clear();

        if (notificationManager != null) {
            List<Notification> lastEvents = notificationManager.getLast5Events();
            List<String> displayNotifications = lastEvents.stream()
                    .map(n -> n.getMessage() + " - " + n.getTimestamp())
                    .toList();
            notificationsList.setItems(FXCollections.observableArrayList(displayNotifications));
        }
    }
}
