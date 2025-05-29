package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.Licence;
import it.unibo.wastemaster.core.models.Employee.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Controller for editing an existing employee.
 * Handles form population and saving of updated data.
 */
public final class EditEmployeeController {

    private Employee employee;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

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
     * Sets the employee to be edited and populates the form fields with the employee's
     * data.
     *
     * @param employee the Employee to edit
     */
    public void setEmployeeToEdit(final Employee employee) {
        this.employee = employee;

        nameField.setText(employee.getName());
        surnameField.setText(employee.getSurname());
        emailField.setText(employee.getEmail());

        streetField.setText(employee.getLocation().getStreet());
        civicField.setText(employee.getLocation().getCivicNumber());
        cityField.setText(employee.getLocation().getCity());
        postalCodeField.setText(employee.getLocation().getPostalCode());

        roleComboBox.getItems().setAll(Role.values());
        roleComboBox.getSelectionModel().select(employee.getRole());

        licenceComboBox.getItems().setAll(Licence.values());
        licenceComboBox.getSelectionModel().select(employee.getLicence());
    }

    /**
     * Handles the save action triggered by the user.
     * Validates and updates the employee data.
     *
     * @param event the action event
     */
    @FXML
    private void handleSave(final ActionEvent event) {
        try {
            Employee original =
                    AppContext.getEmployeeDAO().findByEmail(employee.getEmail());
            if (original == null) {
                DialogUtils.showError("Error", "Employee not found.",
                        AppContext.getOwner());
                return;
            }

            boolean changed = !original.getName().equals(nameField.getText())
                    || !original.getSurname().equals(surnameField.getText())
                    || !original.getLocation().getStreet().equals(streetField.getText())
                    || !original.getLocation().getCivicNumber()
                            .equals(civicField.getText())
                    || !original.getLocation().getCity().equals(cityField.getText())
                    || !original.getLocation().getPostalCode()
                            .equals(postalCodeField.getText())
                    || original.getRole() != roleComboBox.getValue()
                    || original.getLicence() != licenceComboBox.getValue();

            if (!changed) {
                DialogUtils.showError("No changes", "No fields were modified.",
                        AppContext.getOwner());
                return;
            }

            employee.setName(nameField.getText());
            employee.setSurname(surnameField.getText());
            employee.getLocation().setStreet(streetField.getText());
            employee.getLocation().setCivicNumber(civicField.getText());
            employee.getLocation().setCity(cityField.getText());
            employee.getLocation().setPostalCode(postalCodeField.getText());
            employee.setRole(roleComboBox.getValue());
            employee.setLicence(licenceComboBox.getValue());

            AppContext.getEmployeeManager().updateEmployee(employee);

            DialogUtils.showSuccess("Employee updated successfully.",
                    AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(),
                    AppContext.getOwner());
        } catch (Exception e) {
            DialogUtils.showError("Unexpected error", e.getMessage(),
                    AppContext.getOwner());
        }
    }

    /**
     * Aborts the edit and closes the modal.
     *
     * @param event the action event
     */
    @FXML
    private void handleAbortEdit(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
