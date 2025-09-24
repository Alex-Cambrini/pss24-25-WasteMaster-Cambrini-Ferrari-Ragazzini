package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.domain.model.Employee;

import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddTripController {



    @FXML private ComboBox<String> postalCodeCombo;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private ComboBox<Employee> driverCombo;
    @FXML private Label requiredLicence;
    @FXML private ListView<Employee> operatorsList;
    @FXML private DatePicker departureDate;
    @FXML private DatePicker returnDate;
    @FXML private Spinner<Integer> departureTime;
    @FXML private Spinner<Integer> returnTime;

    private TripManager tripManager;
    private TripController tripController;
    private VehicleManager vehicleManager;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    @FXML
    public void initialize() {
        requiredLicence.setVisible(false);
        vehicleCombo.setDisable(true);
        postalCodeCombo.setDisable(true);
        driverCombo.setDisable(true);

        SpinnerValueFactory.IntegerSpinnerValueFactory depHourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
        departureTime.setValueFactory(depHourFactory);

        SpinnerValueFactory.IntegerSpinnerValueFactory retHourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        returnTime.setValueFactory(retHourFactory);
        
        operatorsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        departureDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateControls();
        });
        returnDate.valueProperty().addListener((obs, oldVal, newVal) -> updateControls());
        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && departureDate.getValue() != null && returnDate.getValue() != null
                    && departureTime.getValue() != null && returnTime.getValue() != null) {
                updateAvailableDriver();
                driverCombo.setDisable(false);

                requiredLicence.setText("Min licence to drive: " + vehicleCombo.getValue().getRequiredLicence());
                requiredLicence.setVisible(true);
            } else {
                driverCombo.setDisable(true);
                requiredLicence.setVisible(false);
            }
        });
    }

    private void updateControls() {
        boolean enable = departureDate.getValue() != null && returnDate.getValue() != null;
        vehicleCombo.setDisable(!enable);
        postalCodeCombo.setDisable(!enable);

        if (enable) {
            updateAvailableVehicles();
            updateAvailablePostalCodes();
        }
    }

    private void updateAvailableVehicles() {
        LocalDate dep = departureDate.getValue();
            LocalDate ret = returnDate.getValue();
        if (dep != null && ret != null && !ret.isBefore(dep)) {
            vehicleCombo.setItems(FXCollections.observableArrayList(
                    tripManager.getAvailableVehicles(dep.atStartOfDay(), ret.atStartOfDay())
            ));
        }
    }

    private void updateAvailablePostalCodes() {
        LocalDate dep = departureDate.getValue();
        if (dep != null) {
            postalCodeCombo.setItems(FXCollections.observableArrayList(
                    tripManager.getAvailablePostalCodes(dep)
            ));
        }
    }

    private void updateAvailableDriver() {
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();
        Vehicle vehicle = vehicleCombo.getValue();

        if (depDate != null && retDate != null && depHour != null && retHour != null && vehicle != null) {
            LocalDateTime depDateTime = depDate.atTime(depHour, 0);
            LocalDateTime retDateTime = retDate.atTime(retHour, 0);
            List<Employee.Licence> allowedLicences = vehicleManager.getAllowedLicences(vehicle);
            driverCombo.setItems(FXCollections.observableArrayList(
                    tripManager.getQualifiedDrivers(depDateTime, retDateTime, allowedLicences)
            ));
        }
    }

    @FXML
        private void handleSaveTrip() {
            String postalCode = postalCodeCombo.getValue();
            Vehicle vehicle = vehicleCombo.getValue();
            List<Employee> operators = operatorsList.getSelectionModel().getSelectedItems();
            LocalDate depDate = departureDate.getValue();
            LocalDate retDate = returnDate.getValue();

            if (postalCode.isEmpty() || vehicle == null || operators.isEmpty() || depDate == null || retDate == null) {
                showAlert("All fields are required.");
                return;
            }

            LocalDateTime depDateTime = depDate.atStartOfDay();
            LocalDateTime retDateTime = retDate.atStartOfDay();

           tripManager.createTrip(
            postalCode,
            vehicle,
            operators,
            depDateTime,
            retDateTime,
            new ArrayList<>()
        );

            close();
            if (tripController != null) {
                tripController.loadTrips();
            }
        }

        @FXML
        private void handleAbortTripCreation() {
            close();
        }

        private void close() {
            ((Stage) postalCodeCombo.getScene().getWindow()).close();
        }

        private void showAlert(String msg) {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            alert.showAndWait();
        }

}
