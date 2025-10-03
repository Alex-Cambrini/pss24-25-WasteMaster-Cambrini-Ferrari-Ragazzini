package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Employee.Role;
import it.unibo.wastemaster.domain.service.EmployeeManager;
import it.unibo.wastemaster.viewmodels.EmployeeRow;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for managing the employee view. Handles loading, filtering,
 * adding, editing, and deleting employees, as well as managing the table and search
 * functionalities.
 */
public final class EmployeeController implements AutoRefreshable {

    private static final String FILTER_NAME = "name";
    private static final String FILTER_SURNAME = "surname";
    private static final String FILTER_EMAIL = "email";
    private static final String FILTER_ROLE = "role";
    private static final String FILTER_LICENCE = "licence";
    private static final String FILTER_LOCATION = "location";
    private static final String FILTER_CREATION_DATE = "creationDate";
    private static final int REFRESH_INTERVAL_SECONDS = 30;
    private static final String ERROR_NAVIGATION = "Navigation error";
    private final ObservableList<EmployeeRow> allEmployees = FXCollections.observableArrayList();
    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(FILTER_NAME, FILTER_SURNAME,
            FILTER_EMAIL, FILTER_ROLE, FILTER_LICENCE, FILTER_LOCATION);
    private Timeline refreshTimeline;
    private ContextMenu filterMenu;
    private Stage owner;
    private EmployeeManager employeeManager;

    @FXML
    private Button filterButton;

    @FXML
    private Button addEmployeeButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button editEmployeeButton;

    @FXML
    private Button deleteEmployeeButton;

    @FXML
    private TableView<EmployeeRow> employeeTable;

    @FXML
    private TableColumn<EmployeeRow, String> nameColumn;

    @FXML
    private TableColumn<EmployeeRow, String> surnameColumn;

    @FXML
    private TableColumn<EmployeeRow, String> emailColumn;

    @FXML
    private TableColumn<EmployeeRow, Role> roleColumn;

    @FXML
    private TableColumn<EmployeeRow, Licence> licenceColumn;

    @FXML
    private TableColumn<EmployeeRow, String> locationColumn;

    @FXML
    private TableColumn<EmployeeRow, String> creationDateColumn;

    /**
     * Sets the employee manager used for employee operations.
     *
     * @param employeeManager the EmployeeManager to use
     */
    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    /**
     * Initializes the employee view, table columns, and search/filter logic.
     */
    @FXML
    public void initialize() {
        owner = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_NAME));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_SURNAME));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_EMAIL));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_ROLE));
        licenceColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_LICENCE));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>(FILTER_CREATION_DATE));

        roleColumn.setCellFactory(column -> new TableCell<EmployeeRow, Role>() {
            @Override
            protected void updateItem(final Role item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatEnumOrNone(item));
            }
        });

        licenceColumn.setCellFactory(column -> new TableCell<EmployeeRow, Licence>() {
            @Override
            protected void updateItem(final Licence item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatEnumOrNone(item));
            }
        });

        locationColumn.setText("Location");
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFullLocation()));

        editEmployeeButton.setDisable(true);
        deleteEmployeeButton.setDisable(true);

        employeeTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean selected = newVal != null;
                    editEmployeeButton.setDisable(!selected);
                    deleteEmployeeButton.setDisable(!selected);
                });

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    /**
     * Initializes data and components that depend on external managers or services.
     * This method must be called after all required managers are injected.
     */
    public void initData() {
        System.out.println("[DEBUG] initData EmployeeController called");
        loadEmployee();
    }

    /**
     * Starts the automatic refresh of the employee table.
     */
    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null || employeeManager == null) {
            return;
        }
        refreshTimeline = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_INTERVAL_SECONDS),
                event -> loadEmployee()));
        refreshTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh of the employee table, if it is currently active.
     * This is typically used when the view is being closed or refreshed manually.
     */
    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Loads the employee data from the database and populates the employee table.
     * This method retrieves all employee details, clears the existing list, and adds new
     * rows to the table.
     */
    public void loadEmployee() {
        List<Employee> employees = employeeManager.getAllActiveEmployees();
        allEmployees.clear();

        for (Employee employee : employees) {
            allEmployees.add(new EmployeeRow(employee));
        }

        employeeTable.setItems(FXCollections.observableArrayList(allEmployees));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    /**
     * Handles the search action, filtering the employee table based on the query and active filters.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            employeeTable.setItems(FXCollections.observableArrayList(allEmployees));
            return;
        }

        ObservableList<EmployeeRow> filtered = FXCollections.observableArrayList();

        for (EmployeeRow row : allEmployees) {
            if ((activeFilters.contains(FILTER_NAME)
                    && row.getName().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_SURNAME)
                            && row.getSurname().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_EMAIL)
                            && row.getEmail().toLowerCase().contains(query))
                    || (activeFilters.contains(FILTER_ROLE)
                            && formatEnumOrNone(row.getRole()).toLowerCase()
                                    .contains(query))
                    || (activeFilters.contains(FILTER_LICENCE)
                            && formatEnumOrNone(row.getLicence()).toLowerCase()
                                    .contains(query))
                    || (activeFilters.contains(FILTER_LOCATION)
                            && row.getFullLocation().toLowerCase().contains(query))) {

                filtered.add(row);
            }
        }

        employeeTable.setItems(filtered);
    }

    /**
     * Handles the deletion of the selected employee.
     */
    @FXML
    private void handleDeleteEmployee() {
        EmployeeRow selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select an employee to delete.", owner);
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete this employee?",
                owner);

        if (!confirmed) {
            return;
        }

        Optional<Employee> employeeOpt = employeeManager.findEmployeeByEmail(selected.getEmail());
        if (employeeOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected employee could not be found.", owner);
            return;
        }

        Employee employee = employeeOpt.get();
        boolean success = employeeManager.softDeleteEmployee(employee);
        if (success) {
            DialogUtils.showSuccess("Employee deleted successfully.", owner);
            loadEmployee();
        } else {
            DialogUtils.showError("Deletion Failed",
                    "Unable to delete the selected employee.", owner);
        }
    }

    /**
     * Handles the addition of a new employee.
     */
    @FXML
    private void handleAddEmployee() {
        try {
            Optional<AddEmployeeController> controllerOpt = DialogUtils.showModalWithController("Add Employee",
                    "/layouts/employee/AddEmployeeView.fxml", owner,
                    ctrl -> {
                        ctrl.setEmployeeManager(employeeManager);
                    });

            if (controllerOpt.isPresent()) {
                loadEmployee();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError(ERROR_NAVIGATION,
                    "Could not load Add Employee view.",
                    owner);
        }
    }

    /**
     * Handles the editing of the selected employee.
     */
    @FXML
    private void handleEditEmployee() {
        EmployeeRow selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select an employee to edit.",
                    owner);
            return;
        }

        Optional<Employee> employeeOpt = employeeManager.findEmployeeByEmail(selected.getEmail());
        if (employeeOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "Employee not found.", owner);
            return;
        }

        Employee employee = employeeOpt.get();
        try {
            Optional<EditEmployeeController> controllerOpt = DialogUtils.showModalWithController("Edit Employee",
                    "/layouts/employee/EditEmployeeView.fxml", owner,
                    ctrl -> {
                        ctrl.setEmployeeToEdit(employee);
                        ctrl.setEmployeeManager(employeeManager);
                    });

            if (controllerOpt.isPresent()) {
                loadEmployee();
            }
        } catch (Exception e) {
            DialogUtils.showError(ERROR_NAVIGATION, "Could not load Edit view.",
                    owner);
        }
    }

    /**
     * Handles the reset of the search field and filters.
     */
    @FXML
    private void handleResetSearch() {
        searchField.clear();

        activeFilters.clear();
        activeFilters.addAll(FILTER_NAME, FILTER_SURNAME, FILTER_EMAIL, FILTER_ROLE,
                FILTER_LICENCE, FILTER_LOCATION);

        loadEmployee();
    }

    /**
     * Shows the filter menu for selecting which fields to search.
     *
     * @param event the mouse event triggering the menu
     */
    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { FILTER_NAME, FILTER_SURNAME, FILTER_EMAIL, FILTER_ROLE,
                FILTER_LICENCE, FILTER_LOCATION };
        String[] labels = { "Name", "Surname", "Email", "Role", "Licence", "Location" };

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));

            checkBox.selectedProperty().addListener((obs,
                    wasSelected,
                    isSelected) -> {
                if (isSelected != null && isSelected) {
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

    private String formatEnumOrNone(final Enum<?> value) {
        if (value == null) {
            return "";
        }
        if (value.name().equalsIgnoreCase("none")) {
            return "None";
        }
        String str = value.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Returns to the employee view by loading the appropriate FXML layout. Restores
     * the previous title and loads the employee management screen. Displays an error
     * dialog if the view fails to load.
     */
    public void returnToEmployeeView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance()
                    .loadCenter("/layouts/employee/EmployeeView.fxml");
        } catch (Exception e) {
            DialogUtils.showError(ERROR_NAVIGATION,
                    "Failed to load employee view.",
                    owner);
        }
    }
}
