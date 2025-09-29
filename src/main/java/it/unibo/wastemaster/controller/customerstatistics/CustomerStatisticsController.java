package it.unibo.wastemaster.controller.customerstatistics;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.CollectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CustomerStatisticsController {

    @FXML private Label customerNameLabel;
    @FXML private Label totalInvoicesLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TableView<Invoice> invoicesTable;
    @FXML private TableView<Collection> collectionsTable;
    @FXML private TableColumn<Invoice, String> invoiceIdColumn;

    @FXML private TableColumn<Invoice, String> invoiceDateColumn;
    @FXML private TableColumn<Invoice, String> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, String> invoiceStatusColumn;

    @FXML private TableColumn<Collection, String> collectionIdColumn;
    @FXML private TableColumn<Collection, String> collectionDateColumn;
    @FXML private TableColumn<Collection, String> collectionWasteColumn;
    @FXML private TableColumn<Collection, String> collectionStatusColumn;

    private CustomerManager customerManager;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;
    private Customer customer;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateStatistics();
    }

    @FXML
    public void initialize() {
    clearStatistics();

    // Invoice table columns
    invoiceIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getInvoiceId())));
    invoiceDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIssueDate().toString()));
    invoiceAmountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getAmount())));
    invoiceStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentStatus().toString()));

    // Collection table columns
    collectionIdColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCollectionId())));
    collectionDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCollectionDate().toString()));
    collectionWasteColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWaste().getWasteName()));
    collectionStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCollectionStatus().toString()));
    }

    private void updateStatistics() {
        if (customer == null) {
            clearStatistics();
            return;
        }
        customerNameLabel.setText(customer.getName() + " " + customer.getSurname());

        List<Invoice> invoices = invoiceManager.getAllInvoices().stream()
            .filter(inv -> inv.getCustomer().equals(customer))
            .toList();

        List<Collection> collections = collectionManager.getAllCollections().stream()
            .filter(c -> c.getSchedule().getCustomer().equals(customer))
            .toList();

        totalInvoicesLabel.setText(String.valueOf(invoices.size()));
        totalCollectionsLabel.setText(String.valueOf(collections.size()));
        double totalAmount = invoices.stream().mapToDouble(Invoice::getAmount).sum();
        totalAmountLabel.setText(String.format("%.2f €", totalAmount));

        invoicesTable.setItems(FXCollections.observableArrayList(invoices));
        collectionsTable.setItems(FXCollections.observableArrayList(collections));
    }

    private void clearStatistics() {
        customerNameLabel.setText("-");
        totalInvoicesLabel.setText("-");
        totalCollectionsLabel.setText("-");
        totalAmountLabel.setText("-");
        invoicesTable.getItems().clear();
        collectionsTable.getItems().clear();
    }
}