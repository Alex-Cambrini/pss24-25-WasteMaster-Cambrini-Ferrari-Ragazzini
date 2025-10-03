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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Controller for editing an existing trip.
 * Handles form population, crew selection, validation, and saving of updated trip data.
 */
public final class EditTripController {

    private final ObservableList<Employee> operatorItems =
            FXCollections.observableArrayList();
    private final Map<Integer, BooleanProperty> selectedById = new HashMap<>();
    private final IntegerProperty seatCapacity = new SimpleIntegerProperty(0);
    private Trip tripToEdit;
    private TripController tripController;
    private TripManager tripManager;
    private VehicleManager vehicleManager;

    @FXML
    private Label departureDateTime;

    @FXML
    private Label returnDateTime;

    @FXML
    private Label postalCodeLabel;

    @FXML
    private ComboBox<Vehicle> vehicleCombo;

    @FXML
    private ComboBox<Employee> driverCombo;

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
    private Label operatorsHint;

    /**
     * Sets the trip manager used for updating trips.
     *
     * @param tripManager the TripManager to use
     */
    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    /**
     * Sets the vehicle manager used for vehicle and licence checks.
     *
     * @param vehicleManager the VehicleManager to use
     */
    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    /**
     * Sets the trip to be edited.
     *
     * @param trip the Trip to edit
     */
    public void setTripToEdit(final Trip trip) {
        this.tripToEdit = trip;
    }

    /**
     * Initializes UI components, listeners, and table columns.
     */
    @FXML
    public void initialize() {
        driverCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateAvailableOperators();
            enforceSeatLimit();
            refreshHint();
        });

        vehicleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            seatCapacity.set(getSeatCount(newVal));
            refreshHint();
            updateAvailableDrivers();
            updateAvailableOperators();
        });

        opSelectCol.setCellValueFactory(cd -> selectedPropertyFor(cd.getValue()));
        opSelectCol.setCellFactory(CheckBoxTableCell.forTableColumn(opSelectCol));
        opSelectCol.setEditable(true);

        opNameCol.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(
                        cd.getValue().getName() + " " + cd.getValue().getSurname()));
        opRoleCol.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(String.valueOf(cd.getValue().getRole())));
        opLicCol.setCellValueFactory(
                cd -> new ReadOnlyStringWrapper(
                        String.valueOf(cd.getValue().getLicence())));

        operatorsTable.setItems(operatorItems);
        operatorsTable.setEditable(true);

        refreshHint();
    }

    /**
     * Populates the form fields with the trip's current data.
     */
    public void initData() {
        populateFields();
    }

    private void populateFields() {
        if (tripToEdit == null) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        departureDateTime.setText(tripToEdit.getDepartureTime().format(formatter));
        returnDateTime.setText(tripToEdit.getExpectedReturnTime().format(formatter));
        postalCodeLabel.setText(tripToEdit.getPostalCode());

        updateAvailableVehicles(
                tripToEdit.getDepartureTime().toLocalDate(),
                tripToEdit.getExpectedReturnTime().toLocalDate(),
                tripToEdit.getDepartureTime().getHour(),
                tripToEdit.getExpectedReturnTime().getHour());

        Vehicle currentVehicle = tripToEdit.getAssignedVehicle();
        if (currentVehicle != null) {
            vehicleCombo.setValue(currentVehicle);
        }
        seatCapacity.set(getSeatCount(currentVehicle));
        refreshHint();

        Employee currentDriver = null;
        if (!tripToEdit.getOperators().isEmpty()) {
            currentDriver = tripToEdit.getOperators().get(0);
        }

        updateAvailableDrivers();

        if (currentDriver != null && driverCombo.getItems().contains(currentDriver)) {
            driverCombo.setValue(currentDriver);
        } else if (!driverCombo.getItems().isEmpty()) {
            driverCombo.setValue(driverCombo.getItems().get(0));
        }

        updateAvailableOperators();

        selectedById.values().forEach(p -> p.set(false));
        if (tripToEdit.getOperators().size() > 1) {
            List<Employee> previousOperators = tripToEdit.getOperators()
                    .subList(1, tripToEdit.getOperators().size());
            for (Employee op : previousOperators) {
                BooleanProperty prop = selectedById.get(op.getEmployeeId());
                if (prop != null) {
                    prop.set(true);
                }
            }
        }
        enforceSeatLimit();
        refreshHint();
        operatorsTable.refresh();
    }

    private void updateAvailableVehicles(LocalDate dep, LocalDate ret, int depHour,
                                         int retHour) {
        if (tripManager == null) {
            return;
        }

        Vehicle currentVehicle = tripToEdit.getAssignedVehicle();
        List<Vehicle> allVehicles =
                tripManager.getAvailableVehicles(dep.atTime(depHour, 0),
                        ret.atTime(retHour, 0));
        List<Vehicle> filteredVehicles = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            if (v.getRequiredLicence().equals(currentVehicle.getRequiredLicence())
                    && v.getRequiredOperators()
                    == currentVehicle.getRequiredOperators()) {
                filteredVehicles.add(v);
            }
        }
        vehicleCombo.setItems(FXCollections.observableArrayList(filteredVehicles));

        if (currentVehicle != null) {
            seatCapacity.set(getSeatCount(currentVehicle));
            refreshHint();
        }
    }

    private void updateAvailableOperators() {
        if (tripToEdit == null || tripManager == null) {
            return;
        }

        LocalDateTime depDateTime = tripToEdit.getDepartureTime();
        LocalDateTime retDateTime = tripToEdit.getExpectedReturnTime();
        Employee selectedDriver = driverCombo.getValue();

        if (depDateTime == null || retDateTime == null) {
            return;
        }

        List<Employee> availableOperators = tripManager
                .getAvailableOperatorsExcludeDriverToEdit(depDateTime, retDateTime,
                        selectedDriver, tripToEdit);

        operatorItems.setAll(availableOperators);
        Map<Integer, Boolean> oldSelections = new HashMap<>();
        selectedById.forEach((k, v) -> oldSelections.put(k, v.get()));

        selectedById.clear();
        for (Employee e : availableOperators) {
            boolean wasSelected = oldSelections.getOrDefault(e.getEmployeeId(), false);
            selectedById.put(e.getEmployeeId(), new SimpleBooleanProperty(wasSelected));
        }

        enforceSeatLimit();
        refreshHint();
        operatorsTable.refresh();
    }

    private void updateAvailableDrivers() {
        if (tripToEdit == null || tripManager == null) {
            return;
        }

        LocalDateTime depDateTime = tripToEdit.getDepartureTime();
        LocalDateTime retDateTime = tripToEdit.getExpectedReturnTime();
        Vehicle currentVehicle = vehicleCombo.getValue();

        if (depDateTime == null || retDateTime == null || currentVehicle == null) {
            return;
        }

        List<Employee.Licence> allowedLicences =
                vehicleManager.getAllowedLicences(currentVehicle);
        List<Employee> availableDrivers = tripManager
                .getQualifiedDriversToEdit(depDateTime, retDateTime, allowedLicences,
                        tripToEdit);

        Employee currentDriver = driverCombo.getValue();

        driverCombo.getItems().setAll(availableDrivers);

        if (currentDriver != null && availableDrivers.contains(currentDriver)) {
            driverCombo.setValue(currentDriver);
        } else if (!availableDrivers.isEmpty()) {
            driverCombo.setValue(availableDrivers.get(0));
        }

        seatCapacity.set(getSeatCount(currentVehicle));
        enforceSeatLimit();
        refreshHint();
    }

    private BooleanProperty selectedPropertyFor(Employee e) {
        BooleanProperty p = selectedById.computeIfAbsent(
                e.getEmployeeId(), k -> new SimpleBooleanProperty(false));
        p.addListener((obs, was, is) -> {
            if (is) {
                long ops = selectedById.values().stream().filter(BooleanProperty::get)
                        .count();
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
                .filter(e -> selectedById.getOrDefault(e.getEmployeeId(),
                        new SimpleBooleanProperty(false)).get())
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
        if (v == null) {
            return 0;
        }
        return v.getRequiredOperators();
    }

    /**
     * Handles the update action, validating input and saving the trip changes.
     *
     * @param event the action event from the update button
     */
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

            List<Employee> selectedOperators = operatorItems.stream()
                    .filter(e -> selectedById.getOrDefault(e.getEmployeeId(),
                            new SimpleBooleanProperty(false)).get())
                    .toList();

            List<Employee> newOperatorsList = new ArrayList<>();
            if (selectedDriver != null) {
                newOperatorsList.add(selectedDriver);
            }
            newOperatorsList.addAll(selectedOperators);

            int totalPeople = newOperatorsList.size();
            int required = seatCapacity.get();
            if (required > 0) {
                if (totalPeople < required) {
                    DialogUtils.showError("Validation error",
                            "Not enough crew members selected (" + totalPeople + " / "
                                    + required + ").",
                            AppContext.getOwner());
                    return;
                }
                if (totalPeople > required) {
                    DialogUtils.showError("Validation error",
                            "Too many people for this vehicle (" + totalPeople + " / "
                                    + required + ").",
                            AppContext.getOwner());
                    return;
                }
            }

            boolean changed = !original.getAssignedVehicle().equals(selectedVehicle)
                    || !original.getOperators().equals(newOperatorsList);

            if (!changed) {
                DialogUtils.showError("No changes", "No fields were modified.",
                        AppContext.getOwner());
                return;
            }

            tripToEdit.setAssignedVehicle(selectedVehicle);
            tripToEdit.setOperators(newOperatorsList);

            tripManager.updateTrip(tripToEdit);

            if (tripController != null) {
                tripController.loadTrips();
            }

            DialogUtils.showSuccess("Trip updated successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(),
                    AppContext.getOwner());
        }
    }

    /**
     * Handles aborting the trip edit modal.
     *
     * @param event the action event from the abort button
     */
    @FXML
    private void handleAbortTripEdit(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
