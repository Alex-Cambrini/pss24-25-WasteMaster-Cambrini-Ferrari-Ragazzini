package it.unibo.wastemaster.controller.trip;

import static it.unibo.wastemaster.controller.utils.DialogUtils.closeModal;

import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class AddTripController {

    @FXML
    private ComboBox<String> postalCodeCombo;

    @FXML
    private ComboBox<Vehicle> vehicleCombo;

    @FXML
    private ComboBox<Employee> driverCombo;

    @FXML
    private Label requiredLicence;

    @FXML
    private ListView<Employee> operatorsList;

    @FXML
    private DatePicker departureDate;

    @FXML
    private DatePicker returnDate;

    @FXML
    private Spinner<Integer> departureTime;

    @FXML
    private Spinner<Integer> returnTime;

    private TripManager tripManager;
    private VehicleManager vehicleManager;
    private CollectionManager collectionManager;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
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
            resetControls();
        });
        returnDate.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());
        departureTime.valueProperty()
                .addListener((obs, oldVal, newVal) -> resetControls());
        returnTime.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());

        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            driverCombo.getItems().clear();
            driverCombo.getSelectionModel().clearSelection();
            requiredLicence.setVisible(false);

            if (newVal != null) {
                updateDriverInfo(newVal);
            } else {
                driverCombo.setDisable(true);
            }

            operatorsList.getItems().clear();
        });

        driverCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            operatorsList.getItems().clear();
            if (newVal != null) {
                updateAvailableOperators();
            }
        });

    }

    private void updateDriverInfo(Vehicle vehicle) {
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();

        if (depDate == null || retDate == null || depHour == null || retHour == null) {
            driverCombo.getItems().clear();
            driverCombo.setDisable(true);
            requiredLicence.setVisible(false);
            return;
        }

        LocalDateTime depDateTime = depDate.atTime(depHour, 0);
        LocalDateTime retDateTime = retDate.atTime(retHour, 0);

        List<Employee.Licence> allowedLicences =
                vehicleManager.getAllowedLicences(vehicle);
        List<Employee> drivers = tripManager.getQualifiedDrivers(depDateTime, retDateTime,
                allowedLicences);

        driverCombo.setItems(FXCollections.observableArrayList(drivers));
        driverCombo.setDisable(drivers.isEmpty());
        requiredLicence.setText("Min licence to drive: " + vehicle.getRequiredLicence());
        requiredLicence.setVisible(!drivers.isEmpty());
    }

    private void resetControls() {
        // Pulisci selezioni e nascondi info
        vehicleCombo.getSelectionModel().clearSelection();
        driverCombo.getSelectionModel().clearSelection();
        operatorsList.getItems().clear();
        postalCodeCombo.getSelectionModel().clearSelection();
        postalCodeCombo.getItems().clear();
        requiredLicence.setVisible(false);

        // Disabilita controlli finché non ci sono date e orari validi
        vehicleCombo.setDisable(true);
        postalCodeCombo.setDisable(true);
        driverCombo.setDisable(true);

        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();

        // Se date e orari sono presenti e coerenti, aggiorna veicoli e codici postali
        if (depDate != null && retDate != null && depHour != null && retHour != null
                && !retDate.isBefore(depDate)) {
            vehicleCombo.setDisable(false);
            postalCodeCombo.setDisable(false);

            updateAvailableVehicles(depDate, retDate, depHour, retHour);
            updateAvailablePostalCodes(depDate);
        }
    }

    private void updateAvailableVehicles(LocalDate dep, LocalDate ret, int depHour,
                                         int retHour) {
        vehicleCombo.setItems(FXCollections.observableArrayList(
                tripManager.getAvailableVehicles(
                        dep.atTime(depHour, 0),
                        ret.atTime(retHour, 0)
                )
        ));
    }

    private void updateAvailablePostalCodes(LocalDate dep) {
        postalCodeCombo.setItems(FXCollections.observableArrayList(
                tripManager.getAvailablePostalCodes(dep)
        ));
    }

    private void updateAvailableOperators() {
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();

        if (depDate != null && retDate != null && depHour != null && retHour != null) {
            LocalDateTime depDateTime = depDate.atTime(depHour, 0);
            LocalDateTime retDateTime = retDate.atTime(retHour, 0);
            operatorsList.setItems(FXCollections.observableArrayList(
                    tripManager.getAvailableOperatorsExcludeDriver(depDateTime,
                            retDateTime, driverCombo.getValue())
            ));
        }
    }

    @FXML
    private void handleSaveTrip(final ActionEvent event) {
        String postalCode = postalCodeCombo.getValue();
        Vehicle vehicle = vehicleCombo.getValue();
        List<Employee> operators = new ArrayList<>(operatorsList.getSelectionModel().getSelectedItems());
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        LocalDateTime depDateTime = depDate.atTime(departureTime.getValue(), 0);
        LocalDateTime retDateTime = retDate.atTime(returnTime.getValue(), 0);

        if (postalCode == null || postalCode.isEmpty() || vehicle == null
                || operators == null || operators.isEmpty() || depDate == null
                || retDate == null) {
            showAlert("All fields are required.");
            return;
        }

        if (retDateTime.isBefore(depDateTime)) {
            showAlert("Return date/time must be after departure.");
            return;
        }

        List<Collection> collections =
                collectionManager.getCollectionsByPostalCode(postalCode, depDate);

        System.out.println("AAAAAAAAAAAAAAAA");
        System.out.println(postalCodeCombo.getValue());
        tripManager.createTrip(
                postalCode,
                vehicle,
                operators,
                depDateTime,
                retDateTime,
                collections
        );

        closeModal(event);
    }

    @FXML
    private void handleAbortTripCreation(final ActionEvent event) {
        closeModal(event);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
