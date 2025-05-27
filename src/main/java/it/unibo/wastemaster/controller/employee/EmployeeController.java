package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.viewmodels.EmployeeRow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class EmployeeController {

    private Timeline refreshTimeline;
    private ContextMenu filterMenu;
    private ObservableList<EmployeeRow> allEmployees = FXCollections.observableArrayList();
    private Stage owner;

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            "name", "surname", "email", "role", "licence", "location");

    @FXML
    private Button filterButton;

    @FXML
    private Button addEmployeeButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<EmployeeRow> employeeTable;
    @FXML
    private TableColumn<EmployeeRow, String> nameColumn;
    @FXML
    private TableColumn<EmployeeRow, String> surnameColumn;
    @FXML
    private TableColumn<EmployeeRow, String> emailColumn;
    @FXML
    private TableColumn<EmployeeRow, String> roleColumn;
    @FXML
    private TableColumn<EmployeeRow, String> licenceColumn;
    @FXML
    private TableColumn<EmployeeRow, String> locationColumn;

    @FXML
    public void initialize() {
        owner = (Stage) MainLayoutController.getInstance().getRootPane().getScene().getWindow();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getRole())));
        licenceColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(formatEnum(cellData.getValue().getLicence())));
        locationColumn.setText("Location");
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullLocation()));

        loadEmployee();
        startAutoRefresh();

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> loadEmployee()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadEmployee() {
        List<Employee> employees = AppContext.employeeDAO.findEmployeeDetails();
        allEmployees.clear();

        for (Employee employee : employees) {
            allEmployees.add(new EmployeeRow(employee));
        }

        employeeTable.setItems(FXCollections.observableArrayList(allEmployees));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            employeeTable.setItems(FXCollections.observableArrayList(allEmployees));
            return;
        }

        ObservableList<EmployeeRow> filtered = FXCollections.observableArrayList();

        for (EmployeeRow row : allEmployees) {
            if ((activeFilters.contains("name") && row.getName().toLowerCase().contains(query)) ||
                    (activeFilters.contains("surname") && row.getSurname().toLowerCase().contains(query)) ||
                    (activeFilters.contains("email") && row.getEmail().toLowerCase().contains(query)) ||
                    (activeFilters.contains("role") && formatEnumOrNone(row.getRole()).toLowerCase().contains(query)) ||
                    (activeFilters.contains("licence")
                            && formatEnumOrNone(row.getLicence()).toLowerCase().contains(query))
                    ||
                    (activeFilters.contains("location") && row.getFullLocation().toLowerCase().contains(query))) {

                filtered.add(row);
            }
        }

        employeeTable.setItems(filtered);
    }

    @FXML
    private void handleDeleteEmployee() {
        EmployeeRow selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select an employee to delete.", owner);
            return;
        }

        var employee = AppContext.employeeDAO.findByEmail(selected.getEmail());

        if (employee == null) {
            DialogUtils.showError("Not Found", "The selected employee could not be found.", owner);
            return;
        }

        boolean success = AppContext.employeeManager.softDeleteEmployee(employee);
        if (success) {
            DialogUtils.showSuccess("Employee deleted successfully.", owner);
            loadEmployee();
        } else {
            DialogUtils.showError("Deletion Failed", "Unable to delete the selected employee.", owner);
        }
    }

    @FXML
    private void handleAddEmployee() {
        try {
            Optional<AddEmployeeController> controllerOpt = DialogUtils.showModalWithController(
                    "Add Employee",
                    "/layouts/employee/AddEmployeeView.fxml",
                    owner,
                    ctrl -> {
                        ctrl.setEmployeeController(this);
                    });

            if (controllerOpt.isPresent()) {
                loadEmployee();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Navigation error", "Could not load Add Employee view.", owner);
        }
    }

    @FXML
    private void handleEditEmployee() {
        EmployeeRow selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select an employee to edit.", owner);
            return;
        }

        var employee = AppContext.employeeDAO.findByEmail(selected.getEmail());
        if (employee == null) {
            DialogUtils.showError("Not Found", "Employee not found.", owner);
            return;
        }

        try {
            Optional<EditEmployeeController> controllerOpt = DialogUtils.showModalWithController(
                    "Edit Employee",
                    "/layouts/employee/EditEmployeeView.fxml",
                    owner,
                    ctrl -> {
                        ctrl.setEmployeeToEdit(employee);
                        ctrl.setEmployeeController(this);
                    });

            if (controllerOpt.isPresent()) {
                loadEmployee();
            }
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Edit view.", owner);
        }
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();

        activeFilters.clear();
        activeFilters.addAll("name", "surname", "email", "role", "licence", "location");

        loadEmployee();
    }

    @FXML
    private void showFilterMenu(javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = { "name", "surname", "email", "role", "licence", "location" };
        String[] labels = { "Name", "Surname", "Email", "Role", "Licence", "Location" };

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

    private String formatEnumOrNone(Enum<?> value) {
        if (value == null || value.name().equalsIgnoreCase("none")) {
            return "None";
        }
        return formatEnum(value);
    }

    private String formatEnum(Enum<?> value) {
        if (value == null) {
            return "";
        }
        String lower = value.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public void returnToEmployeeView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance().loadCenter("/layouts/employee/EmployeeView.fxml");
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Failed to load employee view.", owner);
        }
    }
}