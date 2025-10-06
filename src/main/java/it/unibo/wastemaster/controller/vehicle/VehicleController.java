package it.unibo.wastemaster.controller.vehicle;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
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
import javafx.scene.control.TableRow;
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
 * Supports periodic automatic refresh of the vehicle list and interaction with the
 * JavaFX UI.
 */
public final class VehicleController implements AutoRefreshable {

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
    private final ObservableList<VehicleRow> allVehicles =
            FXCollections.observableArrayList();
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
    private Button markMaintenanceButton;

    @FXML
    private Button markOutOfServiceButton;

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

    /**
     * Sets the vehicle manager used for vehicle operations.
     *
     * @param vehicleManager the VehicleManager to use
     */
    public void setVehicleManager(final VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    /**
     * Initializes the controller and sets up table columns, cell factories, and
     * listeners.
     */
    @FXML
    public void initialize() {
        plateColumn.setCellValueFactory(new PropertyValueFactory<>(PLATE));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>(BRAND));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>(MODEL));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>(REGISTRATION_YEAR));
        requiredOperatorColumn.setCellValueFactory(
                new PropertyValueFactory<>(REQUIRED_OPERATORS));
        licenceTypeColumn.setCellValueFactory(new PropertyValueFactory<>(LICENCE_TYPE));
        vehicleStatusColumn.setCellValueFactory(
                new PropertyValueFactory<>(VEHICLE_STATUS));
        lastMaintenanceDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(LAST_MAINTENANCE_DATE));
        nextMaintenanceDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(NEXT_MAINTENANCE_DATE));

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

                    if (selected) {
                        updateButtons(
                                vehicleManager.findVehicleByPlate(newVal.getPlate()).get()
                                        .getVehicleStatus());
                    }
                });
        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());

        vehicleTable.setRowFactory(tv -> new TableRow<VehicleRow>() {
            @Override
            protected void updateItem(final VehicleRow item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    LocalDate nextDate = item.getNextMaintenanceDate();
                    if (nextDate != null && !nextDate.isAfter(LocalDate.now())) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Initializes data and components that depend on external managers.
     * To be called after setting the vehicleManager.
     */
    public void initData() {
        loadVehicles();
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
        lastMaintenanceDateColumn.setCellFactory(
                column -> new TableCell<VehicleRow, LocalDate>() {
                    @Override
                    protected void updateItem(final LocalDate item, final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.toString());
                    }
                });
    }

    private void setNextMaintenanceDateCellFactory() {
        nextMaintenanceDateColumn.setCellFactory(
                column -> new TableCell<VehicleRow, LocalDate>() {
                    @Override
                    protected void updateItem(final LocalDate item, final boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "" : item.toString());
                    }
                });
    }

    /**
     * Updates the text and enabled state of maintenance and service buttons
     * based on the current vehicle status.
     *
     * @param status the current status of the vehicle
     */
    private void updateButtons(final Vehicle.VehicleStatus status) {
        if (status == null) {
            markMaintenanceButton.setDisable(true);
            markOutOfServiceButton.setDisable(true);
            return;
        }

        switch (status) {
            case IN_SERVICE -> {
                markMaintenanceButton.setText("Mark as Maintenance");
                markMaintenanceButton.setDisable(false);

                markOutOfServiceButton.setText("Mark as Out of Service");
                markOutOfServiceButton.setDisable(false);
            }
            case IN_MAINTENANCE -> {
                markMaintenanceButton.setText("Mark as In Service");
                markMaintenanceButton.setDisable(false);

                markOutOfServiceButton.setText("Mark as Out of Service");
                markOutOfServiceButton.setDisable(true);
            }
            case OUT_OF_SERVICE -> {
                markMaintenanceButton.setText("Mark as Maintenance");
                markMaintenanceButton.setDisable(true);

                markOutOfServiceButton.setText("Mark as In Service");
                markOutOfServiceButton.setDisable(false);
            }
            default -> {
                markMaintenanceButton.setDisable(true);
                markOutOfServiceButton.setDisable(true);
            }
        }
    }

    /**
     * Starts the automatic refresh of the vehicle table.
     */
    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null || vehicleManager == null) {
            return;
        }
        refreshTimeline =
                new Timeline(new KeyFrame(Duration.seconds(REFRESH_INTERVAL_SECONDS),
                        event -> loadVehicles()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Stops the automatic refresh of the vehicle table.
     */
    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Loads all vehicles from the manager and updates the table.
     */
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

    /**
     * Handles the action to add a new vehicle.
     */
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

    /**
     * Handles the action to edit the selected vehicle.
     */
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

    /**
     * Handles the maintenance button action for the selected vehicle.
     * If the vehicle is IN_SERVICE, asks for confirmation to set it in maintenance.
     * If the vehicle is IN_MAINTENANCE, asks for confirmation to set it back in service.
     * Executes the action via {@link VehicleManager#handleMaintenanceButton(Vehicle)},
     * reloads the table, and updates the button states.
     */
    @FXML
    private void handleMarkMaintenance() {
        VehicleRow selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Vehicle vehicle = vehicleManager.findVehicleByPlate(selected.getPlate()).get();

        if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_SERVICE
                && !DialogUtils.showConfirmationDialog("Set In Maintenance",
                "Do you want to set this vehicle in maintenance?",
                AppContext.getOwner())) {
            return;
        }

        if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_MAINTENANCE
                && !DialogUtils.showConfirmationDialog("Set In Service",
                "Do you want to set this vehicle in service?",
                AppContext.getOwner())) {
            return;
        }

        vehicleManager.handleMaintenanceButton(vehicle);
        loadVehicles();
        updateButtons(vehicle.getVehicleStatus());
    }

    /**
     * Handles the service/out-of-service button action for the selected vehicle.
     * If the vehicle is IN_SERVICE, asks for confirmation to set it OUT_OF_SERVICE.
     * If the vehicle is OUT_OF_SERVICE, asks for confirmation to set it back IN_SERVICE.
     * Executes the action via {@link VehicleManager#handleServiceButton(Vehicle)},
     * reloads the table, and updates the button states.
     */
    @FXML
    private void handleMarkOutOfService() {
        VehicleRow selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Vehicle vehicle = vehicleManager.findVehicleByPlate(selected.getPlate()).get();

        if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.IN_SERVICE
                && !DialogUtils.showConfirmationDialog("Set Out Of Service",
                "Do you want to set this vehicle out of service?",
                AppContext.getOwner())) {
            return;
        }

        if (vehicle.getVehicleStatus() == Vehicle.VehicleStatus.OUT_OF_SERVICE
                && !DialogUtils.showConfirmationDialog("Set In Service",
                "Do you want to set this vehicle in service?",
                AppContext.getOwner())) {
            return;
        }

        vehicleManager.handleServiceButton(vehicle);
        loadVehicles();
        updateButtons(vehicle.getVehicleStatus());
    }

    /**
     * Handles the action to delete the selected vehicle.
     */
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

    /**
     * Handles the search/filtering of vehicles based on the search field and active
     * filters.
     */
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

    /**
     * Handles the reset of the search field and filter checkboxes.
     */
    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(PLATE, BRAND, MODEL, YEAR, LICENCE_TYPE, VEHICLE_STATUS,
                LAST_MAINTENANCE_DATE, NEXT_MAINTENANCE_DATE);
        loadVehicles();
    }

    /**
     * Shows the filter menu for selecting which fields to search.
     *
     * @param event the mouse event triggering the menu
     */
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

    /**
     * Formats enum values for display in the table.
     *
     * @param value the enum value
     * @return the formatted string
     */
    private String formatEnum(final Enum<?> value) {
        if (value == null) {
            return "";
        }
        String lower = value.name().toLowerCase().replace("_", " ");
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
