package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public final class EditTripController {

    private Trip tripToEdit;
    private TripController tripController;
    private TripManager tripManager;
    private VehicleManager vehicleManager;

    @FXML private Label departureDateTime;
    @FXML private Label returnDateTime;
    @FXML private Label postalCodeLabel;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private ComboBox<Employee> driverCombo;
    @FXML private ListView<Employee> operatorsList;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    public void setTripToEdit(final Trip trip) {
        this.tripToEdit = trip;
    }

    @FXML
    public void initialize() {
        // Imposta il renderer della ListView per gli operatori
        operatorsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Employee emp, boolean empty) {
                super.updateItem(emp, empty);
                if (empty || emp == null) {
                    setText(null);
                } else {
                    setText(emp.getName() + " " + emp.getSurname() + " (" + emp.getEmail() + ")");
                }
            }
        });
        operatorsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Listener sul driver per aggiornare gli operatori disponibili
        driverCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateAvailableOperators());

        // Listener sul veicolo per aggiornare i driver disponibili
        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateAvailableDrivers());

    }

    public void initData() {
        populateFields();
    }

    private void populateFields() {
        if (tripToEdit == null) return;

        departureDateTime.setText(tripToEdit.getDepartureTime().toString());
        returnDateTime.setText(tripToEdit.getExpectedReturnTime().toString());
        postalCodeLabel.setText(tripToEdit.getPostalCode());

        // 1. Aggiorna veicoli disponibili e seleziona quello assegnato
        updateAvailableVehicles(
                tripToEdit.getDepartureTime().toLocalDate(),
                tripToEdit.getExpectedReturnTime().toLocalDate(),
                tripToEdit.getDepartureTime().getHour(),
                tripToEdit.getExpectedReturnTime().getHour()
        );
        Vehicle currentVehicle = tripToEdit.getAssignedVehicle();
        if (currentVehicle != null) {
            vehicleCombo.setValue(currentVehicle);
        }

        // 2. Aggiorna driver disponibili per il veicolo selezionato
        updateAvailableDrivers();

        // 3. Seleziona il driver corrente
        Employee currentDriver = null;
        if (!tripToEdit.getOperators().isEmpty()) {
            currentDriver = tripToEdit.getOperators().get(0);
            if (currentDriver != null && driverCombo.getItems().contains(currentDriver)) {
                driverCombo.setValue(currentDriver);
            } else if (!driverCombo.getItems().isEmpty()) {
                driverCombo.setValue(driverCombo.getItems().get(0));
            }
        }

        updateAvailableOperators();

        if (tripToEdit.getOperators().size() > 1) {
            List<Employee> operators = tripToEdit.getOperators().subList(1, tripToEdit.getOperators().size());
            operatorsList.getSelectionModel().clearSelection();
            for (Employee op : operators) {
                if (operatorsList.getItems().contains(op)) {
                    operatorsList.getSelectionModel().select(op);
                }
            }
        }
    }



    private void updateAvailableVehicles(LocalDate dep, LocalDate ret, int depHour, int retHour) {
        if (tripManager == null)
            return;

        Vehicle currentVehicle = tripToEdit.getAssignedVehicle();
        List<Vehicle> allVehicles = tripManager.getAvailableVehicles(dep.atTime(depHour, 0), ret.atTime(retHour, 0));
        List<Vehicle> filteredVehicles = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            if (v.getRequiredLicence().equals(currentVehicle.getRequiredLicence())
                    && v.getRequiredOperators() == currentVehicle.getRequiredOperators()) {
                filteredVehicles.add(v);
            }
        }
        vehicleCombo.setItems(FXCollections.observableArrayList(filteredVehicles));
    }

    private void updateAvailableOperators() {
        if (tripToEdit == null || tripManager == null) return;

        LocalDateTime depDateTime  = tripToEdit.getDepartureTime();
        LocalDateTime retDateTime = tripToEdit.getExpectedReturnTime();
        Employee selectedDriver = driverCombo.getValue();

        if (depDateTime == null || retDateTime == null) return;

        List<Employee> availableOperators = tripManager
                .getAvailableOperatorsExcludeDriverToEdit(depDateTime, retDateTime, selectedDriver, tripToEdit);

        List<Employee> currentlySelected = new ArrayList<>(operatorsList.getSelectionModel().getSelectedItems());
        operatorsList.getItems().setAll(availableOperators);

        for (Employee op : currentlySelected) {
            if (availableOperators.contains(op)) {
                operatorsList.getSelectionModel().select(op);
            }
        }
    }


    private void updateAvailableDrivers() {
        if (tripToEdit == null || tripManager == null) return;

        LocalDateTime depDateTime = tripToEdit.getDepartureTime();
        LocalDateTime retDateTime = tripToEdit.getExpectedReturnTime();
        Vehicle currentVehicle = vehicleCombo.getValue();

        if (depDateTime == null || retDateTime == null || currentVehicle == null) return;

        List<Employee.Licence> allowedLicences = vehicleManager.getAllowedLicences(currentVehicle);

        List<Employee> availableDrivers = tripManager
                .getQualifiedDriversToEdit(depDateTime, retDateTime, allowedLicences, tripToEdit);

        Employee currentDriver = driverCombo.getValue();

        driverCombo.getItems().setAll(availableDrivers);

        if (currentDriver != null && availableDrivers.contains(currentDriver)) {
            driverCombo.setValue(currentDriver);
        } else if (!availableDrivers.isEmpty()) {
            driverCombo.setValue(availableDrivers.get(0));
        }
    }

    @FXML
    private void handleUpdateTrip(final ActionEvent event) {
        try {
            Optional<Trip> originalOpt = tripManager.getTripById(tripToEdit.getTripId());
            if (originalOpt.isEmpty()) {
                DialogUtils.showError("Error", "Trip not found.", AppContext.getOwner());
                return;
            }

            Trip original = originalOpt.get();

            Vehicle selectedVehicle = vehicleCombo.getValue();
            Employee selectedDriver = driverCombo.getValue();
            List<Employee> selectedOperators = new ArrayList<>(operatorsList.getSelectionModel().getSelectedItems());

            // Ricostruisci la lista completa con il driver in testa
            List<Employee> newOperatorsList = new ArrayList<>();
            newOperatorsList.add(selectedDriver);
            newOperatorsList.addAll(selectedOperators);

            // Controlla se ci sono modifiche
            boolean changed = !original.getAssignedVehicle().equals(selectedVehicle)
                    || !original.getOperators().equals(newOperatorsList);

            if (!changed) {
                DialogUtils.showError("No changes", "No fields were modified.", AppContext.getOwner());
                return;
            }

            // Aggiorna il trip
            tripToEdit.setAssignedVehicle(selectedVehicle);
            tripToEdit.setOperators(newOperatorsList);

            tripManager.updateTrip(tripToEdit);

            if (tripController != null) {
                tripController.loadTrips();
            }

            DialogUtils.showSuccess("Trip updated successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(), AppContext.getOwner());
        }
    }



    @FXML
    private void handleAbortTripEdit(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
