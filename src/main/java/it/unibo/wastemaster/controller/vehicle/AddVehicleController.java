package it.unibo.wastemaster.controller.vehicle;

import static it.unibo.wastemaster.controller.utils.DialogUtils.closeModal;
import static it.unibo.wastemaster.controller.utils.DialogUtils.showError;
import static it.unibo.wastemaster.controller.utils.DialogUtils.showSuccess;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Vehicle.RequiredLicence;
import it.unibo.wastemaster.domain.model.Vehicle.VehicleStatus;
import it.unibo.wastemaster.domain.service.VehicleManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Controller for the view that allows adding a new vehicle. Manages field initialization
 * and saving of the entered data.
 */
public final class AddVehicleController {

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

    private VehicleManager vehicleManager;

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    @FXML
    private void initialize() {
        licenceComboBox.getItems().setAll(RequiredLicence.values());
        licenceComboBox.getSelectionModel().selectFirst();

        statusComboBox.getItems().setAll(VehicleStatus.values());
        statusComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSaveVehicle(final ActionEvent event) {
        try {
            String plate = plateField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            int year = Integer.parseInt(yearField.getText());
            int capacity = Integer.parseInt(capacityField.getText());
            RequiredLicence licence = licenceComboBox.getValue();
            VehicleStatus status = statusComboBox.getValue();

            Vehicle vehicle =
                    new Vehicle(plate, brand, model, year, licence, status, capacity);
            vehicleManager.addVehicle(vehicle);
            showSuccess("Vehicle saved successfully.", AppContext.getOwner());
            closeModal(event);

        } catch (IllegalArgumentException e) {
            showError("Validation error", e.getMessage(), AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unexpected error", e.getMessage(), AppContext.getOwner());
        }
    }

    @FXML
    private void handleAbortVehicleCreation(final ActionEvent event) {
        closeModal(event);
    }
}
