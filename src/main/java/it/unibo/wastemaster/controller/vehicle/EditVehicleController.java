package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Vehicle.RequiredLicence;
import it.unibo.wastemaster.core.models.Vehicle.VehicleStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import static it.unibo.wastemaster.controller.utils.DialogUtils.*;

public class EditVehicleController {

	private Vehicle vehicle;

	@FXML
	private TextField plateField;

	@FXML
	private TextField brandField;

	@FXML
	private TextField modelField;

	@FXML
	private TextField yearField;

	@FXML
	private TextField capacityField;

	@FXML
	private ComboBox<RequiredLicence> licenceComboBox;

	@FXML
	private ComboBox<VehicleStatus> statusComboBox;

	public void setVehicleToEdit(Vehicle vehicle) {
		this.vehicle = vehicle;

		plateField.setText(vehicle.getPlate());
		brandField.setText(vehicle.getBrand());
		modelField.setText(vehicle.getModel());
		yearField.setText(String.valueOf(vehicle.getRegistrationYear()));
		capacityField.setText(String.valueOf(vehicle.getCapacity()));

		licenceComboBox.getItems().setAll(RequiredLicence.values());
		licenceComboBox.getSelectionModel().select(vehicle.getRequiredLicence());

		statusComboBox.getItems().setAll(VehicleStatus.values());
		statusComboBox.getSelectionModel().select(vehicle.getVehicleStatus());
	}

	@FXML
	private void handleUpdateVehicle(ActionEvent event) {
		try {
			Vehicle original = AppContext.getVehicleManager().findVehicleByPlate(vehicle.getPlate());
			if (original == null) {
				showError("Error", "Vehicle not found.", AppContext.getOwner());
				return;
			}

			boolean changed = !original.getBrand().equals(brandField.getText()) ||
					!original.getModel().equals(modelField.getText()) ||
					original.getRegistrationYear() != Integer.parseInt(yearField.getText()) ||
					original.getRequiredLicence() != licenceComboBox.getValue() ||
					original.getVehicleStatus() != statusComboBox.getValue() ||
					original.getCapacity() != Integer.parseInt(capacityField.getText());

			if (!changed) {
				showError("No changes", "No fields were modified.", AppContext.getOwner());
				return;
			}

			vehicle.setBrand(brandField.getText());
			vehicle.setModel(modelField.getText());
			vehicle.setRegistrationYear(Integer.parseInt(yearField.getText()));
			vehicle.setRequiredLicence(licenceComboBox.getValue());
			vehicle.setVehicleStatus(statusComboBox.getValue());
			vehicle.setCapacity(Integer.parseInt(capacityField.getText()));

			AppContext.getVehicleManager().updateVehicle(vehicle);
			showSuccess("Vehicle updated successfully.", AppContext.getOwner());
			closeModal(event);

		} catch (IllegalArgumentException e) {
			showError("Validation error", e.getMessage(), AppContext.getOwner());
		} catch (Exception e) {
			showError("Unexpected error", e.getMessage(), AppContext.getOwner());
		}
	}

	@FXML
	private void handleAbortVehicleEdit(ActionEvent event) {
		closeModal(event);
	}
}