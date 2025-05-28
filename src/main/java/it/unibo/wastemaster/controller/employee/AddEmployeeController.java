package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Controller for the Add Employee modal view. Handles input validation and saving logic.
 */
public final class AddEmployeeController {

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

            AppContext.getEmployeeManager().addEmployee(employee);
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
