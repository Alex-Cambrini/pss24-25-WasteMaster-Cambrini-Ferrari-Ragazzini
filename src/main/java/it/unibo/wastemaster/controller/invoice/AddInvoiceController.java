package it.unibo.wastemaster.controller.invoice;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.InvoiceManager;
import it.unibo.wastemaster.viewmodels.CollectionRow;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Controller for the Add Invoice modal view. Handles selection of customer and
 * collections,
 * and creation of a new invoice.
 */
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
    private boolean updatingFromSelectAll = false;
    private boolean updatingFromRows = false;

    private final ObservableList<CollectionRow> availableCollections =
            FXCollections.observableArrayList();

    /**
     * Initializes the controller, setting up table, buttons, and listeners.
     */
    @FXML
    public void initialize() {
        setupCollectionsTable();
        setupSelectedCountField();
        setupButtons();
        setupSelectAllCheck();
    }

    /**
     * Loads customer data into the combo box.
     */
    public void initData() {
        setupCustomerCombo();
    }

    /**
     * Sets the collection manager used to retrieve collections.
     *
     * @param collectionManager the CollectionManager to use
     */
    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the customer manager used to retrieve customers.
     *
     * @param customerManager the CustomerManager to use
     */
    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Sets the invoice manager used to create invoices.
     *
     * @param invoiceManager the InvoiceManager to use
     */
    public void setInvoiceManager(InvoiceManager invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    private void setupCustomerCombo() {
        List<Customer> activeCustomers = customerManager.getAllActiveCustomers();
        customerCombo.setItems(FXCollections.observableArrayList(activeCustomers));
        customerCombo.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newCustomer) -> {
                    loadCollectionsForCustomer(newCustomer);
                });
    }

    private void loadCollectionsForCustomer(Customer customer) {
        availableCollections.clear();

        if (customer != null) {
            List<Collection> collections =
                    collectionManager.getCompletedNotBilledCollections(customer);
            for (Collection c : collections) {
                CollectionRow row = new CollectionRow(c);
                row.selectedProperty().addListener((obs, old, newVal) -> {
                    updateTotal();
                    if (!updatingFromSelectAll) {
                        updateSelectAllCheck();
                    }
                });
                availableCollections.add(row);
            }
        }
        updateTotal();
        updateSelectAllCheck();
    }

    private void updateSelectAllCheck() {
        if (availableCollections.isEmpty()) {
            selectAllCheck.setIndeterminate(false);
            selectAllCheck.setSelected(false);
            return;
        }

        boolean allSelected =
                availableCollections.stream().allMatch(CollectionRow::isSelected);
        boolean noneSelected =
                availableCollections.stream().noneMatch(CollectionRow::isSelected);

        updatingFromRows = true;
        if (allSelected) {
            selectAllCheck.setIndeterminate(false);
            selectAllCheck.setSelected(true);
        } else if (noneSelected) {
            selectAllCheck.setIndeterminate(false);
            selectAllCheck.setSelected(false);
        } else {
            selectAllCheck.setSelected(false);
            selectAllCheck.setIndeterminate(true);
        }
        updatingFromRows = false;
    }

    private void setupCollectionsTable() {
        collectionsTable.setItems(availableCollections);

        selectCol.setCellValueFactory(cell -> cell.getValue().selectedProperty());
        idCol.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        dateCol.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(
                        cell.getValue().getCollectionDate().toString()));
        scheduleCol.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getZone()));

        collectionsTable.setEditable(true);
        selectCol.setEditable(true);
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
    }

    private void setupSelectAllCheck() {
        selectAllCheck.selectedProperty().addListener((obs, old, newVal) -> {
            if (updatingFromRows) {
                return;
            }
            updatingFromSelectAll = true;
            for (CollectionRow row : availableCollections) {
                row.setSelected(newVal);
            }
            updatingFromSelectAll = false;
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

    /**
     * Handles the abort action, closing the modal dialog.
     */
    @FXML
    private void handleAbortInvoiceCreation() {
        cancelButton.getScene().getWindow().hide();
    }

    /**
     * Handles the save action, creating a new invoice for the selected customer and
     * collections.
     */
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
