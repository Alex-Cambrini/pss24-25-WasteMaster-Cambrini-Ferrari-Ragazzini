package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.utils.ValidateUtils;

import static it.unibo.wastemaster.controller.utils.DialogUtils.*;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddEmployeeController {
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

    private EmployeeController employeeController;

    public void setEmployeeController(EmployeeController controller) {
        this.employeeController = controller;
    }

    @FXML
    public void initialize() {
        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().selectFirst();

        licenceComboBox.getItems().setAll(Licence.values());
        licenceComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSaveEmployee() {
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
            Employee employee = new Employee(name, surname, address, email, phone, role, licence);

            ValidateUtils.validateAll(employee, address);

            AppContext.employeeManager.addEmployee(employee);

            showSuccess("Employee saved successfully.", AppContext.getOwner());

            if (employeeController != null) {
                employeeController.returnToEmployeeView();
            }
        } catch (IllegalArgumentException e) {
            showError("Validation error", e.getMessage(), AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unexpected error", e.getMessage(), AppContext.getOwner());
        }
    }

    @FXML
    private void handleAbortEmployeeCreation() {
        if (employeeController != null) {
            employeeController.returnToEmployeeView();
        }
    }
}
