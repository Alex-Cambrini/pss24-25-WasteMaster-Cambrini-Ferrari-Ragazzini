package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.viewmodels.EmployeeRow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class EmployeeController {

    private Timeline refreshTimeline;
    private ObservableList<EmployeeRow> allEmployees = FXCollections.observableArrayList();

    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            "name", "surname", "email", "role", "licence", "city");

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
    private TableColumn<EmployeeRow, String> cityColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        licenceColumn.setCellValueFactory(new PropertyValueFactory<>("licence"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        loadEmployees();
        startAutoRefresh();

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> loadEmployees()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadEmployees() {
        List<Employee> employees = AppContext.employeeDAO.findAll();
        allEmployees.clear();

        for (Employee e : employees) {
            if (!e.isDeleted()) {
                allEmployees.add(new EmployeeRow(
                        e.getName(),
                        e.getSurname(),
                        e.getEmail(),
                        e.getRole().name(),
                        e.getLicenceType().name(),
                        e.getLocation().getCity()));
            }
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
                    (activeFilters.contains("role") && row.getRole().toLowerCase().contains(query)) ||
                    (activeFilters.contains("licence") && row.getLicence().toLowerCase().contains(query)) ||
                    (activeFilters.contains("city") && row.getCity().toLowerCase().contains(query))) {
                filtered.add(row);
            }
        }

        employeeTable.setItems(filtered);
    }

    @FXML
    private void handleDeleteEmployee() {
        EmployeeRow selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select an employee to delete.");
            return;
        }

        var employee = AppContext.employeeDAO.findByEmail(selected.getEmail());

        if (employee == null) {
            DialogUtils.showError("Not Found", "The selected employee could not be found.");
            return;
        }

        boolean success = AppContext.employeeManager.softDeleteEmployee(employee);
        if (success) {
            DialogUtils.showSuccess("Employee deleted successfully.");
            loadEmployees();
        } else {
            DialogUtils.showError("Deletion Failed", "Unable to delete the selected employee.");
        }
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        loadEmployees();
    }
}