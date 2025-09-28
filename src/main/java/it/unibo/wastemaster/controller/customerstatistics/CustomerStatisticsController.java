package it.unibo.wastemaster.controller.customerstatistics;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Invoice;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.domain.service.CollectionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class CustomerStatisticsController {

    @FXML private ComboBox<Customer> customerCombo;
    @FXML private Label totalInvoicesLabel;
    @FXML private Label totalCollectionsLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TableView<Invoice> invoicesTable;
    @FXML private TableView<Collection> collectionsTable;

    private CustomerManager customerManager;
    private InvoiceManager invoiceManager;
    private CollectionManager collectionManager;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public ComboBox<Customer> getCustomerCombo() {
    return customerCombo;
    }
    
    @FXML
    public void initialize() {
        if (customerManager != null) {
            customerCombo.setItems(FXCollections.observableArrayList(customerManager.getAllCustomers()));
        }
        customerCombo.setOnAction(e -> onCustomerSelected());
        clearStatistics();
    }

    private void onCustomerSelected() {
        Customer selected = customerCombo.getValue();
        if (selected == null) {
            clearStatistics();
            return;
        }
    // Get all invoices for the selected customer
    List<Invoice> invoices = invoiceManager.getAllInvoices().stream()
        .filter(inv -> inv.getCustomer().equals(selected))
        .toList();

    // Get all collections for the selected customer
    List<Collection> collections = collectionManager.getAllCollections().stream()
        .filter(c -> c.getSchedule().getCustomer().equals(selected))
        .toList();

    totalInvoicesLabel.setText(String.valueOf(invoices.size()));
    totalCollectionsLabel.setText(String.valueOf(collections.size()));
    double totalAmount = invoices.stream().mapToDouble(Invoice::getAmount).sum();
    totalAmountLabel.setText(String.format("%.2f €", totalAmount));

        invoicesTable.setItems(FXCollections.observableArrayList(invoices));
        collectionsTable.setItems(FXCollections.observableArrayList(collections));
    }

    private void clearStatistics() {
        totalInvoicesLabel.setText("-");
        totalCollectionsLabel.setText("-");
        totalAmountLabel.setText("-");
        invoicesTable.getItems().clear();
        collectionsTable.getItems().clear();
    }
}