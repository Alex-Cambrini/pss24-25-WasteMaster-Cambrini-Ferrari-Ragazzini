package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.viewmodels.VehicleRow;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for managing the vehicle view.
 * Handles loading, displaying, searching, filtering, adding, editing, and deleting
 * vehicles.
 * Supports periodic automatic refresh of the vehicle list and interaction with the JavaFX
 * UI.
 */
public final class VehicleController {

    private static final int REFRESH_INTERVAL_SECONDS = 30;
    private static final String PLATE = "plate";
    private static final String BRAND = "brand";
    private static final String MODEL = "model";
    private static final String YEAR = "year";
    private static final String LICENCE_TYPE = "licenceType";
    private static final String VEHICLE_STATUS = "vehicleStatus";
    private static final String LAST_MAINTENANCE_DATE = "lastMaintenanceDate";
    private static final String NEXT_MAINTENANCE_DATE = "nextMaintenanceDate";
    private static final String REGISTRATION_YEAR = "registrationYear";
    private static final String REQUIRED_OPERATORS = "requiredOperators";
    private final ObservableList<VehicleRow> allVehicles = FXCollections
            .observableArrayList();
    private final ObservableList<String> activeFilters =
            FXCollections.observableArrayList(PLATE, BRAND, MODEL, YEAR, LICENCE_TYPE,
                    VEHICLE_STATUS, LAST_MAINTENANCE_DATE, NEXT_MAINTENANCE_DATE);
    private Timeline refreshTimeline;
    private ContextMenu filterMenu;
    private VehicleManager vehicleManager;

    @FXML
    private TextField searchField;

    @FXML
    private Button filterButton;

    @FXML
    private Button editVehicleButton;

    @FXML
    private Button deleteVehicleButton;

    @FXML
    private TableView<VehicleRow> vehicleTable;

    @FXML
    private TableColumn<VehicleRow, String> plateColumn;

    @FXML
    private TableColumn<VehicleRow, String> brandColumn;

    @FXML
    private TableColumn<VehicleRow, String> modelColumn;

    @FXML
    private TableColumn<VehicleRow, Integer> yearColumn;

    @FXML
    private TableColumn<VehicleRow, Integer> requiredOperatorColumn;

    @FXML
    private TableColumn<VehicleRow, Vehicle.RequiredLicence> licenceTypeColumn;

    @FXML
    private TableColumn<VehicleRow, Vehicle.VehicleStatus> vehicleStatusColumn;

    @FXML
    private TableColumn<VehicleRow, LocalDate> lastMaintenanceDateColumn;

    @FXML
    private TableColumn<VehicleRow, LocalDate> nextMaintenanceDateColumn;

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    @FXML
    public void initialize() {
        plateColumn.setCellValueFactory(new PropertyValueFactory<>(PLATE));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>(BRAND));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>(MODEL));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>(REGISTRATION_YEAR));
        requiredOperatorColumn.setCellValueFactory(new PropertyValueFactory<>(REQUIRED_OPERATORS));
        licenceTypeColumn.setCellValueFactory(new PropertyValueFactory<>(LICENCE_TYPE));
        vehicleStatusColumn.setCellValueFactory(new PropertyValueFactory<>(VEHICLE_STATUS));
        lastMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_MAINTENANCE_DATE));
        nextMaintenanceDateColumn.setCellValueFactory(new PropertyValueFactory<>(NEXT_MAINTENANCE_DATE));

        setLicenceTypeCellFactory();
        setVehicleStatusCellFactory();
        setLastMaintenanceDateCellFactory();
        setNextMaintenanceDateCellFactory();

        editVehicleButton.setDisable(true);
        deleteVehicleButton.setDisable(true);

        vehicleTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean selected = newVal != null;
                    editVehicleButton.setDisable(!selected);
                    deleteVehicleButton.setDisable(!selected);
                });

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
    }

    /**
     * Initializes data and components that depend on external managers.
     * To be called after setting the vehicleManager.
     */
    public void initData() {
        loadVehicles();
        startAutoRefresh();
    }

    private void setLicenceTypeCellFactory() {
        licenceTypeColumn.setCellFactory(
                column -> new TableCell<VehicleRow, Vehicle.RequiredLicence>() {

                    @Override
                    protected void updateItem(final Vehicle.RequiredLicence item,
                                              final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : formatEnum(item));
                    }
                });
    }

    private void setVehicleStatusCellFactory() {
        vehicleStatusColumn.setCellFactory(
                column -> new TableCell<VehicleRow, Vehicle.VehicleStatus>() {

                    @Override
                    protected void updateItem(final Vehicle.VehicleStatus item,
                                              final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : formatEnum(item));
                    }
                });
    }

    private void setLastMaintenanceDateCellFactory() {
        lastMaintenanceDateColumn
                .setCellFactory(column -> new TableCell<VehicleRow, LocalDate>() {

                    @Override
                    protected void updateItem(final LocalDate item, final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.toString());
                    }
                });
    }

    private void setNextMaintenanceDateCellFactory() {
        nextMaintenanceDateColumn
                .setCellFactory(column -> new TableCell<VehicleRow, LocalDate>() {

                    @Override
                    protected void updateItem(final LocalDate item, final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.toString());
                    }
                });
    }

    private void startAutoRefresh() {
        refreshTimeline =
                new Timeline(new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS),
                        event -> loadVehicles()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    private void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadVehicles() {
        List<Vehicle> vehicles = vehicleManager.findAllVehicle();
        allVehicles.clear();

        for (Vehicle vehicle : vehicles) {
            allVehicles.add(new VehicleRow(vehicle));
        }

        vehicleTable.setItems(FXCollections.observableArrayList(allVehicles));

        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    @FXML
    private void handleAddVehicle() {
        try {
            Stage mainStage = (Stage) MainLayoutController.getInstance().getRootPane()
                    .getScene().getWindow();

            Optional<AddVehicleController> controllerOpt =
                    DialogUtils.showModalWithController("Add Vehicle",
                            "/layouts/vehicle/AddVehicleView.fxml", mainStage,
                            ctrl -> {
                                ctrl.setVehicleManager(vehicleManager);
                            });

            if (controllerOpt.isPresent()) {
                loadVehicles();
            }
        } catch (IOException e) {
            DialogUtils.showError("Navigation error", "Could not load Add Vehicle view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditVehicle() {
        VehicleRow selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a vehicle to edit.",
                    AppContext.getOwner());
            return;
        }

        Optional<Vehicle> vehicleOpt =
                vehicleManager.findVehicleByPlate(selected.getPlate());
        if (vehicleOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Vehicle not found.",
                    AppContext.getOwner());
            return;
        }

        Vehicle vehicle = vehicleOpt.get();
        try {
            Optional<EditVehicleController> controllerOpt =
                    DialogUtils.showModalWithController("Edit Vehicle",
                            "/layouts/vehicle/EditVehicleView.fxml",
                            AppContext.getOwner(),
                            ctrl -> {
                                ctrl.setVehicleToEdit(vehicle);
                                ctrl.setVehicleManager(vehicleManager);

                            });

            if (controllerOpt.isPresent()) {
                loadVehicles();
            }
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Edit view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteVehicle() {
        VehicleRow selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a vehicle to delete.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete this vehicle?",
                AppContext.getOwner()
        );

        if (!confirmed) {
            return;
        }

        Optional<Vehicle> vehicleOpt =
                vehicleManager.findVehicleByPlate(selected.getPlate());

        if (vehicleOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected vehicle could not be found.",
                    AppContext.getOwner());
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        boolean success = vehicleManager.deleteVehicle(vehicle);

        if (success) {
            DialogUtils.showSuccess("Vehicle deleted successfully.",
                    AppContext.getOwner());
            loadVehicles();
        } else {
            DialogUtils.showError("Deletion Failed",
                    "Unable to delete the selected vehicle.",
                    AppContext.getOwner());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            vehicleTable.setItems(FXCollections.observableArrayList(allVehicles));
            return;
        }
        ObservableList<VehicleRow> filtered = FXCollections.observableArrayList();

        for (VehicleRow row : allVehicles) {
            if ((activeFilters.contains(PLATE)
                    && row.getPlate().toLowerCase().contains(query))
                    || (activeFilters.contains(BRAND)
                    && row.getBrand().toLowerCase().contains(query))
                    || (activeFilters.contains(MODEL)
                    && row.getModel().toLowerCase().contains(query))
                    || (activeFilters.contains(YEAR)
                    && String.valueOf(row.getRegistrationYear()).contains(query))
                    || (activeFilters.contains(LICENCE_TYPE)
                    && row.getLicenceType().name().equalsIgnoreCase(query))
                    || (activeFilters.contains(VEHICLE_STATUS)
                    && formatEnum(row.getVehicleStatus()).toLowerCase()
                    .contains(query))
                    || (activeFilters.contains(LAST_MAINTENANCE_DATE)
                    && row.getLastMaintenanceDate().toString().toLowerCase()
                    .contains(query))
                    || (activeFilters.contains(NEXT_MAINTENANCE_DATE)
                    && row.getNextMaintenanceDate().toString().toLowerCase()
                    .contains(query))) {
                filtered.add(row);
            }
        }
        vehicleTable.setItems(filtered);
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(PLATE, BRAND, MODEL, YEAR, LICENCE_TYPE, VEHICLE_STATUS,
                LAST_MAINTENANCE_DATE, NEXT_MAINTENANCE_DATE);
        loadVehicles();
    }

    @FXML
    private void showFilterMenu(final MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();

        String[] fields = {PLATE, BRAND, MODEL, YEAR, LICENCE_TYPE,
                VEHICLE_STATUS, LAST_MAINTENANCE_DATE, NEXT_MAINTENANCE_DATE};
        String[] labels = {PLATE, BRAND, MODEL, YEAR, LICENCE_TYPE, VEHICLE_STATUS,
                LAST_MAINTENANCE_DATE, NEXT_MAINTENANCE_DATE};

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected != null && isSelected) {
                    if (!activeFilters.contains(key)) {
                        activeFilters.add(key);
                    }
                } else {
                    activeFilters.remove(key);
                }
                handleSearch();
            });

            CustomMenuItem item = new CustomMenuItem(checkBox);
            item.setHideOnClick(false);
            filterMenu.getItems().add(item);
        }

        filterMenu.show(filterButton, event.getScreenX(), event.getScreenY());
    }

    private String formatEnum(final Enum<?> value) {
        if (value == null) {
            return "";
        }
        String lower = value.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
