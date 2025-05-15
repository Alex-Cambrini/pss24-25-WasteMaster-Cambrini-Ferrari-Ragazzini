package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.core.context.AppContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;

import java.util.List;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.viewmodels.CustomerRow;

public class CustomersController {

    private Timeline refreshTimeline;
    private ContextMenu filterMenu;
    private ObservableList<CustomerRow> allCustomers = FXCollections.observableArrayList();

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            "name", "surname", "email", "street", "civic", "city", "postal");

    @FXML
    private Button filterButton;

    @FXML
    private Button addCustomerButton;

    @FXML
    private javafx.scene.control.TextField searchField;

    @FXML
    private TableView<CustomerRow> customerTable;
    @FXML
    private TableColumn<CustomerRow, String> nameColumn;
    @FXML
    private TableColumn<CustomerRow, String> surnameColumn;
    @FXML
    private TableColumn<CustomerRow, String> emailColumn;
    @FXML
    private TableColumn<CustomerRow, String> streetColumn;
    @FXML
    private TableColumn<CustomerRow, String> civicColumn;
    @FXML
    private TableColumn<CustomerRow, String> cityColumn;
    @FXML
    private TableColumn<CustomerRow, String> postalColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        civicColumn.setCellValueFactory(new PropertyValueFactory<>("civic"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        loadCustomers();
        startAutoRefresh();
        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> loadCustomers()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadCustomers() {
        List<Object[]> rawData = AppContext.customerDAO.findCustomerDetails();
        allCustomers.clear();

        for (Object[] row : rawData) {
            allCustomers.add(new CustomerRow(
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    (String) row[3],
                    (String) row[4],
                    (String) row[5],
                    (String) row[6]));
        }

        customerTable.setItems(FXCollections.observableArrayList(allCustomers));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    @FXML
    private void handleAddCustomer() {
        try {
            MainLayoutController.getInstance().setPageTitle("Add Customer");
            AddCustomerController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/customer/AddCustomerView.fxml");
            controller.setCustomerController(this);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Add Customer view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a customer to delete.");
            return;
        }

        var customer = AppContext.customerDAO.findByEmail(selected.getEmail());

        if (customer == null) {
            DialogUtils.showError("Not Found", "The selected customer could not be found.");
            return;
        }

        boolean success = AppContext.customerManager.softDeleteCustomer(customer);
        if (success) {
            DialogUtils.showSuccess("Customer deleted successfully.");
            loadCustomers();
        } else {
            DialogUtils.showError("Deletion Failed", "Unable to delete the selected customer.");
        }
    }

    @FXML
    private void handleEditCustomer() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a customer to edit.");
            return;
        }

        var customer = AppContext.customerDAO.findByEmail(selected.getEmail());
        if (customer == null) {
            DialogUtils.showError("Not Found", "Customer not found.");
            return;
        }

        try {
            MainLayoutController.getInstance().setPageTitle("Edit Customer");
            EditCustomerController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/customer/EditCustomerView.fxml");
            controller.setCustomerToEdit(customer);
            controller.setCustomerController(this);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Edit view.");
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            customerTable.setItems(FXCollections.observableArrayList(allCustomers));
            return;
        }

        ObservableList<CustomerRow> filtered = FXCollections.observableArrayList();

        for (CustomerRow row : allCustomers) {
            if ((activeFilters.contains("name") && row.getName().toLowerCase().contains(query)) ||
                    (activeFilters.contains("surname") && row.getSurname().toLowerCase().contains(query)) ||
                    (activeFilters.contains("email") && row.getEmail().toLowerCase().contains(query)) ||
                    (activeFilters.contains("street") && row.getStreet().toLowerCase().contains(query)) ||
                    (activeFilters.contains("civic") && row.getCivic().toLowerCase().contains(query)) ||
                    (activeFilters.contains("city") && row.getCity().toLowerCase().contains(query)) ||
                    (activeFilters.contains("postal") && row.getPostalCode().toLowerCase().contains(query))) {
                filtered.add(row);
            }
        }

        customerTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();

        activeFilters.clear();
        activeFilters.addAll("name", "surname", "email", "street", "civic", "city", "postal");

        loadCustomers();
    }

    @FXML
    private void showFilterMenu(javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { "name", "surname", "email", "street", "civic", "city", "postal" };
        String[] labels = { "Name", "Surname", "Email", "Street", "Civic", "City", "Postal Code" };

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    if (!activeFilters.contains(key)) {
                        activeFilters.add(key);
                    }
                } else {
                    activeFilters.remove(key);
                }
                handleSearch();
            });

            CustomMenuItem item = new CustomMenuItem(checkBox);
            item.setHideOnClick(false);
            filterMenu.getItems().add(item);
        }

        filterMenu.show(filterButton, event.getScreenX(), event.getScreenY());
    }

    public void returnToCustomerView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance().loadCenter("/layouts/customer/CustomersView.fxml");
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Failed to load customer view.");
        }
    }

}