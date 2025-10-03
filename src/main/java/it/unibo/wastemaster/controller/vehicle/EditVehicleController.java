package it.unibo.wastemaster.controller.vehicle;

import static it.unibo.wastemaster.controller.utils.DialogUtils.closeModal;
import static it.unibo.wastemaster.controller.utils.DialogUtils.showError;
import static it.unibo.wastemaster.controller.utils.DialogUtils.showSuccess;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.model.Vehicle.RequiredLicence;
import it.unibo.wastemaster.domain.model.Vehicle.VehicleStatus;
import it.unibo.wastemaster.domain.service.VehicleManager;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Controller for the view to edit an existing vehicle.
 * Manages loading the vehicle data into fields, updating the modified data, and
 * handling UI events.
 */
public final class EditVehicleController {

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
    private TextField requiredOperatorsField;

    @FXML
    private ComboBox<RequiredLicence> licenceComboBox;

    @FXML
    private ComboBox<VehicleStatus> statusComboBox;

    private VehicleManager vehicleManager;

    /**
     * Sets the vehicle manager used for vehicle operations.
     *
     * @param vehicleManager the VehicleManager to use
     */
    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    /**
     * Sets the vehicle to be edited and populates the form fields.
     * Subclasses overriding this method must call super.setVehicleToEdit().
     *
     * @param vehicle the vehicle to edit
     */
    public void setVehicleToEdit(final Vehicle vehicle) {
        this.vehicle = vehicle;

        plateField.setText(vehicle.getPlate());
        brandField.setText(vehicle.getBrand());
        modelField.setText(vehicle.getModel());
        yearField.setText(String.valueOf(vehicle.getRegistrationYear()));
        requiredOperatorsField.setText(String.valueOf(vehicle.getRequiredOperators()));

        licenceComboBox.getItems().setAll(RequiredLicence.values());
        licenceComboBox.getSelectionModel().select(vehicle.getRequiredLicence());

        statusComboBox.getItems().setAll(VehicleStatus.values());
        statusComboBox.getSelectionModel().select(vehicle.getVehicleStatus());
    }

    /**
     * Handles the update action, validating input and saving the vehicle changes.
     *
     * @param event the action event from the update button
     */
    @FXML
    private void handleUpdateVehicle(final ActionEvent event) {
        try {
            Optional<Vehicle> vehicleOpt =
                    vehicleManager.findVehicleByPlate(vehicle.getPlate());
            if (vehicleOpt.isEmpty()) {
                showError("Error", "Vehicle not found.", AppContext.getOwner());
                return;
            }

            Vehicle vehicle = vehicleOpt.get();
            boolean changed =
                    !vehicle.getPlate().equalsIgnoreCase(plateField.getText().trim())
                            || !vehicle.getBrand().equals(brandField.getText())
                            || !vehicle.getModel().equals(modelField.getText())
                            || vehicle.getRegistrationYear() != Integer
                            .parseInt(yearField.getText())
                            || vehicle.getRequiredLicence() != licenceComboBox.getValue()
                            || vehicle.getVehicleStatus() != statusComboBox.getValue()
                            || vehicle.getRequiredOperators() != Integer
                            .parseInt(requiredOperatorsField.getText());

            if (!changed) {
                showError("No changes", "No fields were modified.",
                        AppContext.getOwner());
                return;
            }

            vehicle.setPlate(plateField.getText().trim().toUpperCase());
            vehicle.setBrand(brandField.getText());
            vehicle.setModel(modelField.getText());
            vehicle.setRegistrationYear(Integer.parseInt(yearField.getText()));
            vehicle.setRequiredLicence(licenceComboBox.getValue());
            vehicle.setVehicleStatus(statusComboBox.getValue());
            vehicle.setRequiredOperators(
                    Integer.parseInt(requiredOperatorsField.getText()));

            vehicleManager.updateVehicle(vehicle);
            showSuccess("Vehicle updated successfully.", AppContext.getOwner());
            closeModal(event);

        } catch (IllegalArgumentException e) {
            showError("Validation error", e.getMessage(), AppContext.getOwner());
        } catch (Exception e) {
            showError("Unexpected error", e.getMessage(), AppContext.getOwner());
        }
    }

    /**
     * Handles aborting the vehicle edit modal.
     *
     * @param event the action event from the abort button
     */
    @FXML
    private void handleAbortVehicleEdit(final ActionEvent event) {
        closeModal(event);
    }
}
