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
import java.util.Map;
import java.util.HashMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

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
    private TableView<Employee> operatorsTable;
    @FXML
    private TableColumn<Employee, Boolean> opSelectCol;
    @FXML
    private TableColumn<Employee, String> opNameCol;
    @FXML
    private TableColumn<Employee, String> opRoleCol;
    @FXML
    private TableColumn<Employee, String> opLicCol;

    @FXML
    private DatePicker departureDate;
    @FXML
    private DatePicker returnDate;
    @FXML
    private Spinner<Integer> departureTime;
    @FXML
    private Spinner<Integer> returnTime;

    @FXML
    private Label operatorsHint;

    private TripManager tripManager;
    private VehicleManager vehicleManager;
    private CollectionManager collectionManager;

    private final ObservableList<Employee> operatorItems = FXCollections.observableArrayList();
    private final Map<Integer, BooleanProperty> selectedById = new HashMap<>();
    private final IntegerProperty seatCapacity = new SimpleIntegerProperty(0);

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

        departureTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        returnTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));

        opSelectCol.setCellValueFactory(cd -> selectedPropertyFor(cd.getValue()));
        opSelectCol.setCellFactory(CheckBoxTableCell.forTableColumn(opSelectCol));
        opSelectCol.setEditable(true);

        opNameCol.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(cd.getValue().getName() + " " + cd.getValue().getSurname()));
        opRoleCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getRole())));
        opLicCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getLicence())));

        operatorsTable.setItems(operatorItems);
        operatorsTable.setEditable(true);

        departureDate.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());
        returnDate.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());
        departureTime.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());
        returnTime.valueProperty().addListener((obs, oldVal, newVal) -> resetControls());

        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            driverCombo.getItems().clear();
            driverCombo.getSelectionModel().clearSelection();
            requiredLicence.setVisible(false);

            seatCapacity.set(getSeatCount(newVal));
            refreshHint();

            if (newVal != null) {
                updateDriverInfo(newVal);
            } else {
                driverCombo.setDisable(true);
            }
            operatorsTable.getItems().clear();
            selectedById.clear();
            refreshHint();
        });

        driverCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateAvailableOperators();
            enforceSeatLimit();
            refreshHint();
        });

        refreshHint();
    }

    private void resetControls() {
        vehicleCombo.getSelectionModel().clearSelection();
        driverCombo.getSelectionModel().clearSelection();
        operatorsTable.getItems().clear();
        selectedById.clear();

        postalCodeCombo.getSelectionModel().clearSelection();
        postalCodeCombo.getItems().clear();
        requiredLicence.setVisible(false);

        vehicleCombo.setDisable(true);
        postalCodeCombo.setDisable(true);
        driverCombo.setDisable(true);

        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();

        if (depDate != null && retDate != null && depHour != null && retHour != null
                && !retDate.isBefore(depDate)) {
            vehicleCombo.setDisable(false);
            postalCodeCombo.setDisable(false);

            updateAvailableVehicles(depDate, retDate, depHour, retHour);
            updateAvailablePostalCodes(depDate);
        }
        seatCapacity.set(0);
        refreshHint();
    }

    private void updateAvailableVehicles(LocalDate dep, LocalDate ret, int depHour, int retHour) {
        if (tripManager == null)
            return;
        vehicleCombo.setItems(FXCollections.observableArrayList(
                tripManager.getAvailableVehicles(dep.atTime(depHour, 0), ret.atTime(retHour, 0))));
    }

    private void updateAvailablePostalCodes(LocalDate dep) {
        if (tripManager == null)
            return;
        postalCodeCombo.setItems(FXCollections.observableArrayList(
                tripManager.getAvailablePostalCodes(dep)));
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

        List<Employee.Licence> allowedLicences = vehicleManager.getAllowedLicences(vehicle);
        List<Employee> drivers = tripManager.getQualifiedDrivers(depDateTime, retDateTime, allowedLicences);

        driverCombo.setItems(FXCollections.observableArrayList(drivers));
        driverCombo.setDisable(drivers.isEmpty());
        requiredLicence.setText("Min licence to drive: " + vehicle.getRequiredLicence());
        requiredLicence.setVisible(!drivers.isEmpty());
        refreshHint();
    }

    private void updateAvailableOperators() {
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        Integer depHour = departureTime.getValue();
        Integer retHour = returnTime.getValue();

        if (depDate != null && retDate != null && depHour != null && retHour != null) {
            LocalDateTime depDateTime = depDate.atTime(depHour, 0);
            LocalDateTime retDateTime = retDate.atTime(retHour, 0);

            var list = tripManager.getAvailableOperatorsExcludeDriver(
                    depDateTime, retDateTime, driverCombo.getValue());

            operatorItems.setAll(list);
            selectedById.clear();
            for (Employee e : list) {
                selectedById.put(e.getEmployeeId(), new SimpleBooleanProperty(false));
            }
            enforceSeatLimit();
            refreshHint();
            operatorsTable.refresh();
        }
    }

    private BooleanProperty selectedPropertyFor(Employee e) {
        BooleanProperty p = selectedById.computeIfAbsent(
                e.getEmployeeId(), k -> new SimpleBooleanProperty(false));
        p.addListener((obs, was, is) -> {
            if (is) {
                long ops = selectedById.values().stream().filter(BooleanProperty::get).count();
                long total = ops + (driverCombo.getValue() != null ? 1 : 0);
                if (seatCapacity.get() > 0 && total > seatCapacity.get()) {
                    p.set(false);
                    return;
                }
            }
            refreshHint();
        });
        return p;
    }

    private void enforceSeatLimit() {
        int cap = seatCapacity.get();
        if (cap <= 0) {
            selectedById.values().forEach(prop -> prop.set(false));
            return;
        }
        int driverCount = (driverCombo.getValue() != null ? 1 : 0);
        int allowedOps = Math.max(0, cap - driverCount);

        var selected = operatorItems.stream()
                .filter(e -> selectedById.getOrDefault(e.getEmployeeId(), new SimpleBooleanProperty(false)).get())
                .toList();

        if (selected.size() > allowedOps) {
            for (int i = allowedOps; i < selected.size(); i++) {
                selectedById.get(selected.get(i).getEmployeeId()).set(false);
            }
        }
    }

    private void refreshHint() {
        long ops = selectedById.values().stream().filter(BooleanProperty::get).count();
        long total = ops + (driverCombo.getValue() != null ? 1 : 0);
        operatorsHint.setText(total + " / " + seatCapacity.get() + " selected");
    }

    private int getSeatCount(Vehicle v) {
        if (v == null)
            return 0;
        return v.getRequiredOperators();
    }

    @FXML
    private void handleSaveTrip(final ActionEvent event) {
        String postalCode = postalCodeCombo.getValue();
        Vehicle vehicle = vehicleCombo.getValue();
        Employee driver = driverCombo.getValue();
        LocalDate depDate = departureDate.getValue();
        LocalDate retDate = returnDate.getValue();
        LocalDateTime depDateTime = depDate != null ? depDate.atTime(departureTime.getValue(), 0) : null;
        LocalDateTime retDateTime = retDate != null ? retDate.atTime(returnTime.getValue(), 0) : null;

        if (postalCode == null || postalCode.isEmpty() ||
                vehicle == null || depDate == null || retDate == null ||
                depDateTime == null || retDateTime == null) {
            showAlert("All fields are required.");
            return;
        }

        if (retDateTime.isBefore(depDateTime)) {
            showAlert("Return date/time must be after departure.");
            return;
        }

        if (driver == null) {
            showAlert("Please select a driver.");
            return;
        }

        List<Employee.Licence> allowedLicences = vehicleManager.getAllowedLicences(vehicle);
        if (!allowedLicences.contains(driver.getLicence())) {
            showAlert("The selected driver does not have a suitable licence for this vehicle.");
            return;
        }

        List<Employee> operators = operatorItems.stream()
                .filter(e -> selectedById.getOrDefault(e.getEmployeeId(), new SimpleBooleanProperty(false)).get())
                .toList();

        int totalPeople = 1 + operators.size();

        int required = seatCapacity.get();
        if (totalPeople < required) {
            showAlert("Not enough crew members selected (" + totalPeople + " / " + required
                    + "). Please add more operators to meet the required crew size.");
            return;
        }
        if (totalPeople > seatCapacity.get()) {
            showAlert("Too many people for this vehicle (" + totalPeople + " / " + seatCapacity.get() + ").");
            return;
        }

        List<Employee> finalCrew = new ArrayList<>(operators);
        if (!finalCrew.contains(driver)) {
            finalCrew.add(0, driver);
        } else {
            finalCrew.remove(driver);
            finalCrew.add(0, driver);
        }

        List<Collection> collections = collectionManager.getCollectionsByPostalCode(postalCode, depDate);

        tripManager.createTrip(
                postalCode,
                vehicle,
                finalCrew,
                depDateTime,
                retDateTime,
                collections);

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
