package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EditVehicleController {

	private Vehicle vehicle;
	private VehicleController vehicleController;

	@FXML
	private TextField plateField;
	@FXML
	private TextField brandField;
	@FXML
	private TextField modelField;
	@FXML
	private TextField yearField;
	@FXML
	private ComboBox<Vehicle.LicenceType> licenceComboBox;
	@FXML
	private ComboBox<Vehicle.VehicleStatus> statusComboBox;

	public void setVehicleController(VehicleController controller) {
		this.vehicleController = controller;
	}

	public void setVehicleToEdit(Vehicle vehicle) {
		this.vehicle = vehicle;

		plateField.setText(vehicle.getPlate());
		brandField.setText(vehicle.getBrand());
		modelField.setText(vehicle.getModel());
		yearField.setText(String.valueOf(vehicle.getRegistrationYear()));
		licenceComboBox.getItems().setAll(Vehicle.LicenceType.values());
		licenceComboBox.getSelectionModel().select(vehicle.getLicenceType());
		statusComboBox.getItems().setAll(Vehicle.VehicleStatus.values());
		statusComboBox.getSelectionModel().select(vehicle.getVehicleStatus());
	}

	@FXML
	private void handleUpdateVehicle() {
		try {
			Vehicle original = AppContext.vehicleManager.findVehicleByPlate(vehicle.getPlate());
			if (original == null) {
				DialogUtils.showError("Error", "Vehicle not found.");
				return;
			}

			boolean changed = !original.getBrand().equals(brandField.getText()) ||
					!original.getModel().equals(modelField.getText()) ||
					original.getRegistrationYear() != Integer.parseInt(yearField.getText()) ||
					original.getLicenceType() != licenceComboBox.getValue() ||
					original.getVehicleStatus() != statusComboBox.getValue();

			if (!changed) {
				DialogUtils.showError("No changes", "No fields were modified.");
				return;
			}

			vehicle.setBrand(brandField.getText());
			vehicle.setModel(modelField.getText());
			vehicle.setRegistrationYear(Integer.parseInt(yearField.getText()));
			vehicle.setLicenceType(licenceComboBox.getValue());
			vehicle.setVehicleStatus(statusComboBox.getValue());

			AppContext.vehicleManager.updateVehicle(vehicle);
			DialogUtils.showSuccess("Vehicle updated successfully.");
			vehicleController.returnToVehicleView();

		} catch (IllegalArgumentException e) {
			DialogUtils.showError("Validation error", e.getMessage());
		} catch (Exception e) {
			DialogUtils.showError("Unexpected error", e.getMessage());
		}
	}

	@FXML
	private void handleAbortVehicleEdit() {
		vehicleController.returnToVehicleView();
	}
}