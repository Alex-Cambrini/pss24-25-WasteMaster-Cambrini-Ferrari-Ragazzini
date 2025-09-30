package it.unibo.wastemaster.controller.dashboard;

import it.unibo.wastemaster.domain.service.*;
import it.unibo.wastemaster.domain.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class DashboardController {

    @FXML private Label totalCustomersLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label totalTripsLabel;
    @FXML private Label invoicesToPayLabel;
    @FXML private ListView<String> notificationsList;

    private CustomerManager customerManager;
    private CollectionManager collectionManager;
    private InvoiceManager invoiceManager;
    private TripManager tripManager;

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

    @FXML
    public void initialize() {
        
    }

    
}