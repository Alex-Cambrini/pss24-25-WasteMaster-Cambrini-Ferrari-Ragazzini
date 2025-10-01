package it.unibo.wastemaster.controller.dashboard;

import it.unibo.wastemaster.domain.service.*;
import it.unibo.wastemaster.domain.model.*;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private Employee currentUser;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }
    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }
    public void setCurrentUser(Employee currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    public void initialize() {
    }

    public void updateDashboard() {

        if (customerManager != null)
            totalCustomersLabel.setText(String.valueOf(customerManager.getAllCustomers().size()));
        if (collectionManager != null)
            totalCollectionsLabel.setText(String.valueOf(collectionManager.getAllCollections().size()));
        if (tripManager != null && currentUser != null)
            totalTripsLabel.setText(String.valueOf(tripManager.getTripsForCurrentUser(currentUser).size()));
        if (invoiceManager != null) {
            long toPay = invoiceManager.getAllInvoices().stream()
                .filter(inv -> inv.getPaymentStatus() != Invoice.PaymentStatus.PAID)
                .count();
            invoicesToPayLabel.setText(String.valueOf(toPay));
        }

        if (collectionsChart != null && collectionManager != null) {
        collectionsChart.getData().clear();

        XYChart.Series<String, Number> cancelledSeries = new XYChart.Series<>();
        cancelledSeries.setName("Cancelled"); 

        XYChart.Series<String, Number> toPaySeries = new XYChart.Series<>();
        toPaySeries.setName("To Pay"); 

        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Completed"); 

        List<String> monthOrder = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

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

        List<String> notifications = new ArrayList<>();

        if (collectionManager != null && !collectionManager.getAllCollections().isEmpty()) {
            Collection lastCollection = collectionManager.getAllCollections().stream()
                .max(Comparator.comparing(Collection::getCollectionDate))
                .orElse(null);
            if (lastCollection != null) {
                String customerName = lastCollection.getSchedule().getCustomer().getSurname();
                notifications.add("Last collection: " + customerName + ", " + lastCollection.getCollectionDate());
            }
        }

        if (tripManager != null && currentUser != null && !tripManager.getTripsForCurrentUser(currentUser).isEmpty()) {
            Trip lastTrip = tripManager.getTripsForCurrentUser(currentUser).stream()
                .max(Comparator.comparing(Trip::getDepartureTime))
                .orElse(null);
            if (lastTrip != null) {
                String vehiclePlate = lastTrip.getAssignedVehicle() != null ? lastTrip.getAssignedVehicle().getPlate() : "N/A";
                notifications.add("Last trip: " + vehiclePlate + ", " + lastTrip.getDepartureTime());
            }
        }

        if (invoiceManager != null && !invoiceManager.getAllInvoices().isEmpty()) {
            Invoice lastInvoice = invoiceManager.getAllInvoices().stream()
                .max(Comparator.comparing(Invoice::getIssueDate))
                .orElse(null);
            if (lastInvoice != null) {
                notifications.add("Last invoice: #" + lastInvoice.getInvoiceId() + ", " + lastInvoice.getIssueDate());
            }
        }

        if (customerManager != null && !customerManager.getAllCustomers().isEmpty()) {
            List<Customer> customers = customerManager.getAllCustomers();
            Customer lastCustomer = customers.get(customers.size() - 1);
            notifications.add("New customer: " + lastCustomer.getSurname());
        }

        if (invoiceManager != null && !invoiceManager.getAllInvoices().isEmpty()) {
            Optional<Invoice> nextDueOpt = invoiceManager.getAllInvoices().stream()
                .filter(inv -> inv.getPaymentStatus() != Invoice.PaymentStatus.PAID)
                .min(Comparator.comparing(Invoice::getIssueDate));
            nextDueOpt.ifPresent(nextDue -> {
                notifications.add("Invoice due soon: #" + nextDue.getInvoiceId());
            });
        }

        List<String> displayNotifications = notifications.isEmpty()
            ? List.of()
            : List.of(notifications.get(notifications.size() - 1));

        notificationsList.setItems(FXCollections.observableArrayList(displayNotifications));
    }
}