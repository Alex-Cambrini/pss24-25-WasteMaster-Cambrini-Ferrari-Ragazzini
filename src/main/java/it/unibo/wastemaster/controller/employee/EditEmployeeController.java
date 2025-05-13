package it.unibo.wastemaster.controller.employee;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Employee;
import it.unibo.wastemaster.core.models.Employee.LicenceType;
import it.unibo.wastemaster.core.models.Employee.Role;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EditEmployeeController {

	private Employee employee;
	private EmployeeController employeeController;

	@FXML
	private TextField nameField;
	@FXML
	private TextField surnameField;
	@FXML
	private TextField emailField;
	@FXML
	private ComboBox<Role> roleComboBox;
	@FXML
	private ComboBox<LicenceType> licenceComboBox;
	@FXML
	private TextField cityField;

	public void setEmployeeController(EmployeeController controller) {
		this.employeeController = controller;
	}

	public void setEmployeeToEdit(Employee employee) {
		this.employee = employee;

		nameField.setText(employee.getName());
		surnameField.setText(employee.getSurname());
		emailField.setText(employee.getEmail());
		cityField.setText(employee.getLocation().getCity());

		roleComboBox.getItems().setAll(Role.values());
		roleComboBox.getSelectionModel().select(employee.getRole());

		licenceComboBox.getItems().setAll(LicenceType.values());
		licenceComboBox.getSelectionModel().select(employee.getLicenceType());
	}

	@FXML
	private void handleUpdateEmployee() {
		try {
			Employee original = AppContext.employeeDAO.findByEmail(employee.getEmail());
			if (original == null) {
				DialogUtils.showError("Error", "Employee not found.");
				return;
			}

			boolean changed =
					!original.getName().equals(nameField.getText()) ||
					!original.getSurname().equals(surnameField.getText()) ||
					!original.getLocation().getCity().equals(cityField.getText()) ||
					original.getRole() != roleComboBox.getValue() ||
					original.getLicenceType() != licenceComboBox.getValue();

			if (!changed) {
				DialogUtils.showError("No changes", "No fields were modified.");
				return;
			}

			employee.setName(nameField.getText());
			employee.setSurname(surnameField.getText());
			employee.setRole(roleComboBox.getValue());
			employee.setLicenceType(licenceComboBox.getValue());
			employee.getLocation().setCity(cityField.getText());

			AppContext.employeeManager.updateEmployee(employee);
			DialogUtils.showSuccess("Employee updated successfully.");
			employeeController.returnToEmployeeView();

		} catch (IllegalArgumentException e) {
			DialogUtils.showError("Validation error", e.getMessage());
		} catch (Exception e) {
			DialogUtils.showError("Unexpected error", e.getMessage());
		}
	}

	@FXML
	private void handleAbortEdit() {
		employeeController.returnToEmployeeView();
	}
}