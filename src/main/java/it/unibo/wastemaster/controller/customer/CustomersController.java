package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.viewmodels.CustomerRow;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 * Controller for managing the customers view, including search, filters and CRUD
 * operations.
 */
public final class CustomersController {

    private static final String FIELD_NAME = "name";
    private static final String FIELD_SURNAME = "surname";
    private static final String FIELD_EMAIL = "email";
    private static final String FILTER_LOCATION = "location";
    private static final String NAVIGATION_ERROR = "Navigation error";
    private static final int REFRESH_SECONDS = 30;

    private Timeline refreshTimeline;
    private ContextMenu filterMenu;

    private final ObservableList<CustomerRow> allCustomers =
            FXCollections.observableArrayList();

    private final ObservableList<String> activeFilters =
            FXCollections.observableArrayList(FIELD_NAME, FIELD_SURNAME,
                    FIELD_EMAIL, FILTER_LOCATION);

    @FXML
    private Button filterButton;

    @FXML
    private Button addCustomerButton;

    @FXML
    private Button editCustomerButton;

    @FXML
    private Button deleteCustomerButton;

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
    private TableColumn<CustomerRow, String> locationColumn;

    /**
     * Initializes the customer view with columns, search and auto-refresh logic.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_NAME));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_SURNAME));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_EMAIL));
        locationColumn.setText("Location");
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFullLocation()));

        loadCustomers();
        startAutoRefresh();

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        customerTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean rowSelected = newVal != null;
                    editCustomerButton.setDisable(!rowSelected);
                    deleteCustomerButton.setDisable(!rowSelected);
                });
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_SECONDS),
                event -> loadCustomers()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh of the customer table.
     */
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    /**
     * Loads all customers from the database and updates the customer table.
     */
    public void loadCustomers() {
        List<Customer> customers = AppContext.getCustomerDAO().findCustomerDetails();
        allCustomers.clear();
        for (Customer customer : customers) {
            allCustomers.add(new CustomerRow(customer));
        }
        customerTable.setItems(FXCollections.observableArrayList(allCustomers));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    @FXML
    private void handleAddCustomer() {
        try {
            Optional<AddCustomerController> controllerOpt =
                    DialogUtils.showModalWithController("Add Customer",
                            "/layouts/customer/AddCustomerView.fxml",
                            AppContext.getOwner(), ctrl -> {
                            });

            if (controllerOpt.isPresent()) {
                loadCustomers();
            }
        } catch (Exception e) {
            DialogUtils.showError(NAVIGATION_ERROR, "Could not load Add Customer view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a customer to delete.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete this customer?",
                AppContext.getOwner()
        );

        if (!confirmed) {
            return;
        }

        var customer = AppContext.getCustomerDAO().findByEmail(selected.getEmail());
        if (customer == null) {
            DialogUtils.showError("Not Found",
                    "The selected customer could not be found.", AppContext.getOwner());
            return;
        }

        boolean success = AppContext.getCustomerManager().softDeleteCustomer(customer);
        if (success) {
            loadCustomers();
        } else {
            DialogUtils.showError("Deletion Failed",
                    "Unable to delete the selected customer.", AppContext.getOwner());
        }
    }

    @FXML
    private void handleEditCustomer() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a customer to edit.",
                    AppContext.getOwner());
            return;
        }

        var customer = AppContext.getCustomerDAO().findByEmail(selected.getEmail());
        if (customer == null) {
            DialogUtils.showError("Not Found", "Customer not found.",
                    AppContext.getOwner());
            return;
        }

        try {
            Optional<EditCustomerController> controllerOpt =
                    DialogUtils.showModalWithController("Edit Customer",
                            "/layouts/customer/EditCustomerView.fxml",
                            AppContext.getOwner(), ctrl -> {
                                ctrl.setCustomerToEdit(customer);
                                ctrl.setCustomerController(this);
                            });

            if (controllerOpt.isPresent()) {
                loadCustomers();
            }
        } catch (Exception e) {
            DialogUtils.showError(NAVIGATION_ERROR, "Could not load Edit Customer view.",
                    AppContext.getOwner());
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
            if (matchesQuery(row, query)) {
                filtered.add(row);
            }
        }
        customerTable.setItems(filtered);
    }

    private boolean matchesQuery(final CustomerRow row, final String query) {
        return (activeFilters.contains(FIELD_NAME)
                && row.getName().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_SURNAME)
                        && row.getSurname().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_EMAIL)
                        && row.getEmail().toLowerCase().contains(query))
                || (activeFilters.contains(FILTER_LOCATION)
                && row.getFullLocation().toLowerCase().contains(query));
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FIELD_NAME, FIELD_SURNAME, FIELD_EMAIL, FILTER_LOCATION);
        loadCustomers();
    }

    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();
        String[] fields = {FIELD_NAME, FIELD_SURNAME, FIELD_EMAIL, FILTER_LOCATION};
        String[] labels = {"Name", "Surname", "Email", "Location"};

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected.booleanValue()) {
                    activeFilters.add(key);
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

    /**
     * Returns to the main customers view from a modal or sub-view.
     */
    public void returnToCustomerView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance()
                    .loadCenter("/layouts/customer/CustomersView.fxml");
        } catch (Exception e) {
            DialogUtils.showError(NAVIGATION_ERROR, "Failed to load customer view.",
                    AppContext.getOwner());
        }
    }
}
