package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Vehicle;
import it.unibo.wastemaster.core.models.Vehicle.LicenceType;
import it.unibo.wastemaster.core.models.Vehicle.VehicleStatus;

import static it.unibo.wastemaster.controller.utils.DialogUtils.*;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class AddVehicleController {

	@FXML
	private TextField plateField;
	@FXML
	private TextField brandField;
	@FXML
	private TextField modelField;
	@FXML
	private TextField yearField;
	@FXML
	private ComboBox<LicenceType> licenceComboBox;
	@FXML
	private ComboBox<VehicleStatus> statusComboBox;

	private VehicleController vehicleController;

	public void setVehicleController(VehicleController controller) {
		this.vehicleController = controller;
	}

	@FXML
	public void initialize() {
		licenceComboBox.getItems().setAll(LicenceType.values());
		licenceComboBox.getSelectionModel().selectFirst();

		statusComboBox.getItems().setAll(VehicleStatus.values());
		statusComboBox.getSelectionModel().selectFirst();
	}

	@FXML
	private void handleSaveVehicle() {
		try {
			String plate = plateField.getText();
			String brand = brandField.getText();
			String model = modelField.getText();
			int year = Integer.parseInt(yearField.getText());
			LicenceType licence = licenceComboBox.getValue();
			VehicleStatus status = statusComboBox.getValue();

			Vehicle vehicle = new Vehicle(plate, brand, model, year, licence, status);
			AppContext.vehicleManager.addVehicle(vehicle);
			showSuccess("Vehicle saved successfully.");

			if (vehicleController != null) {
				vehicleController.returnToVehicleView();
			}
		} catch (IllegalArgumentException e) {
			showError("Validation error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			showError("Unexpected error", e.getMessage());
		}
	}

	@FXML
	private void handleAbortVehicleCreation() {
		if (vehicleController != null) {
			vehicleController.returnToVehicleView();
		}
	}
}