package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.viewmodels.CollectionRow;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.*;

import java.util.List;

public class AddInvoiceController {

    @FXML
    private ComboBox<Customer> customerCombo;
    @FXML
    private CheckBox selectAllCheck;
    @FXML
    private TableView<CollectionRow> collectionsTable;
    @FXML
    private TableColumn<CollectionRow, Boolean> selectCol;
    @FXML
    private TableColumn<CollectionRow, Integer> idCol;
    @FXML
    private TableColumn<CollectionRow, String> dateCol;
    @FXML
    private TableColumn<CollectionRow, String> scheduleCol;
    @FXML
    private Label selectedCountField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private CollectionManager collectionManager;
    private CustomerManager customerManager;
    private InvoiceManager invoiceManager;

    private ObservableList<CollectionRow> availableCollections = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupCollectionsTable();
        setupSelectedCountField();
        setupButtons();
        setupSelectAllCheck();
    }

    public void initData() {
        setupCustomerCombo();
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    private void setupCustomerCombo() {
        List<Customer> activeCustomers = customerManager.getAllActiveCustomers();
        customerCombo.setItems(FXCollections.observableArrayList(activeCustomers));
        customerCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, newCustomer) -> {
            loadCollectionsForCustomer(newCustomer);
        });
    }

    private void loadCollectionsForCustomer(Customer customer) {
        availableCollections.clear();

        if (customer != null) {
            List<Collection> collections = collectionManager.getCompletedNotBilledCollections(customer);
            for (Collection c : collections) {
                CollectionRow row = new CollectionRow(c);
                row.selectedProperty().addListener((obs, old, newVal) -> updateTotal());
                availableCollections.add(row);
            }
        }
        updateTotal();
    }

    private void setupCollectionsTable() {
        collectionsTable.setItems(availableCollections);

        selectCol.setCellValueFactory(cell -> cell.getValue().selectedProperty());
        idCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        dateCol.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getCollectionDate().toString()));
        scheduleCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getZone()));

        collectionsTable.setEditable(true);
        selectCol.setEditable(true);
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
    }

    private void setupSelectAllCheck() {
        selectAllCheck.selectedProperty().addListener((obs, old, newVal) -> {
            for (CollectionRow row : availableCollections) {
                row.setSelected(newVal);
            }
            updateTotal();
        });
    }

    private void updateTotal() {
        long count = availableCollections.stream()
                .filter(CollectionRow::isSelected)
                .count();
        selectedCountField.setText(String.valueOf(count));
    }

    private void setupSelectedCountField() {
        selectedCountField.setText("0");
    }

    private void setupButtons() {
        cancelButton.setOnAction(e -> handleAbortInvoiceCreation());
        saveButton.setOnAction(e -> handleSaveInvoice());
    }

    @FXML
    private void handleAbortInvoiceCreation() {
        // chiudi finestra/modal
        cancelButton.getScene().getWindow().hide();
    }

    @FXML
    private void handleSaveInvoice() {
        Customer selectedCustomer = customerCombo.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Select a customer first.");
            return;
        }

        List<Collection> selectedCollections = availableCollections.stream()
                .filter(CollectionRow::isSelected)
                .map(CollectionRow::getCollection)
                .toList();

        if (selectedCollections.isEmpty()) {
            showAlert("Select at least one collection.");
            return;
        }

        invoiceManager.createInvoice(selectedCustomer, selectedCollections);
        saveButton.getScene().getWindow().hide();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }
}
