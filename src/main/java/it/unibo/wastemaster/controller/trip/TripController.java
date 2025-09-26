package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.viewmodels.TripRow;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 * Controller for managing the trips view, including search, filters and CRUD
 * operations.
 */
public final class TripController {

    private static final String FIELD_ID = "id";
    private static final String FIELD_POSTAL_CODES = "postalCodes";
    private static final String FIELD_VEHICLE_MODEL = "vehicleModel";
    private static final String FIELD_VEHICLE_CAPACITY = "vehicleCapacity";
    private static final String FIELD_OPERATORS = "operators";
    private static final String FIELD_DEPARTURE = "departure";
    private static final String FIELD_RETURN = "returnTime";
    private static final String FIELD_STATUS = "status";
    private static final String NAVIGATION_ERROR = "Navigation error";
    private static final int REFRESH_SECONDS = 30;

    private final ObservableList<TripRow> allTrips = FXCollections.observableArrayList();
    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL, FIELD_VEHICLE_CAPACITY, FIELD_OPERATORS,
            FIELD_STATUS);
    private TripManager tripManager;
    private VehicleManager vehicleManager;
    private CollectionManager collectionManager;
    private Timeline refreshTimeline;
    private ContextMenu filterMenu;

    @FXML
    private Button filterButton;
    @FXML
    private Button editTripButton;
    @FXML
    private Button deleteTripButton;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<TripRow> tripTable;
    @FXML
    private TableColumn<TripRow, String> postalCodeColumn;
    @FXML
    private TableColumn<TripRow, String> vehicleColumn;
    @FXML
    private TableColumn<TripRow, String> vehicleModelColumn;
    @FXML
    private TableColumn<TripRow, Integer> vehicleCapacityColumn;
    @FXML
    private TableColumn<TripRow, String> operatorsColumn;
    @FXML
    private TableColumn<TripRow, String> departureColumn;
    @FXML
    private TableColumn<TripRow, String> returnColumn;
    @FXML
    private TableColumn<TripRow, String> statusColumn;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setVehicleManager(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Initializes the trip view with columns, search and auto-refresh logic.
     */
    @FXML
    public void initialize() {
        tripManager = AppContext.getServiceFactory().getTripManager();
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_POSTAL_CODES));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_VEHICLE_MODEL));
        vehicleCapacityColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_VEHICLE_CAPACITY));
        operatorsColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_OPERATORS));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_DEPARTURE));
        returnColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_RETURN));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_STATUS));

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
        tripTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean rowSelected = newVal != null;
                    editTripButton.setDisable(!rowSelected);
                    deleteTripButton.setDisable(!rowSelected);
                });
        tripTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean rowSelected = newVal != null;
                    if (rowSelected) {
                        String status = newVal.getStatus();
                        boolean disabled = "CANCELED".equals(status) || "COMPLETED".equals(status);
                        editTripButton.setDisable(disabled);
                        deleteTripButton.setDisable(disabled);
                    } else {
                        editTripButton.setDisable(true);
                        deleteTripButton.setDisable(true);
                    }
                });

        loadTrips();
    }

    public void initData() {
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_SECONDS),
                event -> loadTrips()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    /**
     * Loads trips visible to the current user and updates the trip table.
     * Operators see only their assigned trips, while admins and office workers see
     * all trips.
     */
    public void loadTrips() {
        Employee currentUser = AppContext.getCurrentAccount().getEmployee();
        List<Trip> trips = tripManager.getTripsForCurrentUser(currentUser);
        allTrips.clear();
        for (Trip trip : trips) {
            allTrips.add(new TripRow(trip));
        }
        tripTable.setItems(FXCollections.observableArrayList(allTrips));
        if (!searchField.getText().isBlank()) {
            handleSearch();
        }
    }

    @FXML
    private void handleAddTrip() {
        try {
            Optional<AddTripController> controllerOpt = DialogUtils.showModalWithController("Add Trip",
                    "/layouts/trip/AddTripView.fxml",
                    AppContext.getOwner(), ctrl -> {
                        ctrl.setTripManager(tripManager);
                        ctrl.setVehicleManager(vehicleManager);
                        ctrl.setCollectionManager(collectionManager);
                    });
            if (controllerOpt.isPresent()) {
                loadTrips();
            }
        } catch (Exception e) {
            DialogUtils.showError(NAVIGATION_ERROR, "Could not load Add Trip view.",
                    AppContext.getOwner());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteTrip() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to cancel.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Cancellation",
                "Are you sure you want to cancel this trip?",
                AppContext.getOwner());
        if (!confirmed)
            return;

        Optional<Trip> tripOpt = tripManager.getTripById(selected.getIdAsInt());
        if (tripOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected trip could not be found.", AppContext.getOwner());
            return;
        }

        Trip trip = tripOpt.get();
        boolean success = tripManager.softDeleteTrip(trip);
        trip.getCollections().clear();
        if (success) {
            loadTrips();
        } else {
            DialogUtils.showError("Cancellation Failed",
                    "Unable to cancel the selected trip.", AppContext.getOwner());
        }
    }

    @FXML
    private void handleEditTrip() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to edit.",
                    AppContext.getOwner());
            return;
        }

        Optional<Trip> tripOpt = tripManager.getTripById(selected.getIdAsInt());
        if (tripOpt.isEmpty()) {
            DialogUtils.showError("Not Found", "Trip not found.",
                    AppContext.getOwner());
            return;
        }

        try {
            Optional<EditTripController> controllerOpt = DialogUtils.showModalWithController("Edit Trip",
                    "/layouts/trip/EditTripView.fxml",
                    AppContext.getOwner(), ctrl -> {
                        ctrl.setTripToEdit(tripOpt.get());
                        ctrl.setTripController(this);
                        ctrl.setTripManager(tripManager);
                    });

            controllerOpt.ifPresent(ctrl -> loadTrips());
        } catch (Exception e) {
            DialogUtils.showError(NAVIGATION_ERROR, "Could not load Edit Trip view.",
                    AppContext.getOwner());
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            tripTable.setItems(FXCollections.observableArrayList(allTrips));
            return;
        }

        ObservableList<TripRow> filtered = FXCollections.observableArrayList();
        for (TripRow row : allTrips) {
            if (matchesQuery(row, query)) {
                filtered.add(row);
            }
        }
        tripTable.setItems(filtered);
    }

    private boolean matchesQuery(final TripRow row, final String query) {
        return (activeFilters.contains(FIELD_ID)
                && row.getId().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_POSTAL_CODES)
                        && row.getPostalCodes().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_VEHICLE_MODEL)
                        && row.getVehicleModel().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_VEHICLE_CAPACITY)
                        && String.valueOf(row.getVehicleCapacity()).contains(query))
                || (activeFilters.contains(FIELD_OPERATORS)
                        && row.getOperators().toLowerCase().contains(query))
                || (activeFilters.contains(FIELD_STATUS)
                        && row.getStatus().toLowerCase().contains(query));
    }

    @FXML
    private void handleResetSearch() {
        searchField.clear();
        activeFilters.clear();
        activeFilters.addAll(FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL, FIELD_VEHICLE_CAPACITY,
                FIELD_OPERATORS, FIELD_STATUS);
        loadTrips();
    }

    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();
        String[] fields = { FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL, FIELD_VEHICLE_CAPACITY,
                FIELD_OPERATORS, FIELD_STATUS };
        String[] labels = { "ID", "Postal Codes", "Vehicle", "Vehicle Capacity", "Operators",
                "Status" };

        for (int i = 0; i < fields.length; i++) {
            String key = fields[i];
            String label = labels[i];

            CheckBox checkBox = new CheckBox(label);
            checkBox.setSelected(activeFilters.contains(key));
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected.booleanValue()) {
                    activeFilters.add(key);
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
}
