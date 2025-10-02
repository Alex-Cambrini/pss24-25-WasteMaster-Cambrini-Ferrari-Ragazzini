package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Employee.Licence;
import it.unibo.wastemaster.domain.model.Employee.Role;
import it.unibo.wastemaster.domain.model.Location;
import it.unibo.wastemaster.domain.service.EmployeeManager;
import it.unibo.wastemaster.infrastructure.utils.ValidateUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Add Employee modal view. Handles input validation and saving logic.
 */
public final class AddEmployeeController {

    private EmployeeManager employeeManager;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordCheckField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField civicField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private ComboBox<Licence> licenceComboBox;

    /**
     * Sets the employee manager used to persist new employees.
     *
     * @param employeeManager the EmployeeManager to use
     */
    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    /**
     * Initializes combo boxes with default values.
     */
    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().selectFirst();

        licenceComboBox.getItems().setAll(Licence.values());
        licenceComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Handles the save operation of the employee.
     *
     * @param event the action event triggering the save
     */
    @FXML
    private void handleSaveEmployee(final ActionEvent event) {
        String password = passwordField.getText();
        String passwordCheck = passwordCheckField.getText();

        if (!password.equals(passwordCheck)) {
            DialogUtils.showError("Password mismatch",
                    "The two passwords do not match.",
                    AppContext.getOwner());
            return;
        }

        try {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String street = streetField.getText();
            String civic = civicField.getText();
            String city = cityField.getText();
            String postalCode = postalCodeField.getText();
            Role role = roleComboBox.getValue();
            Licence licence = licenceComboBox.getValue();

            Location address = new Location(street, civic, city, postalCode);
            Employee employee =
                    new Employee(name, surname, address, email, phone, role, licence);

            ValidateUtils.validateAll(employee, address);

            employeeManager.addEmployee(employee, password);
            DialogUtils.showSuccess("Employee saved successfully.",
                    AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(),
                    AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Unexpected error", e.getMessage(),
                    AppContext.getOwner());
        }
    }

    /**
     * Handles aborting the employee creation modal.
     *
     * @param event the action event triggering the abort
     */
    @FXML
    private void handleAbortEmployeeCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
