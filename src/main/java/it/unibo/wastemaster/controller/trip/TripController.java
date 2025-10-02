package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.collection.CollectionController;
import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.AutoRefreshable;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.NotificationService;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.viewmodels.TripRow;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Set;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 * Controller for managing the trips view, including search, filters and CRUD
 * operations.
 */
public final class TripController implements AutoRefreshable {

    private NotificationService notificationService;

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

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
            FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL,
            FIELD_VEHICLE_CAPACITY, FIELD_OPERATORS,
            FIELD_STATUS);

    private TripManager tripManager;
    private VehicleManager vehicleManager;
    private CollectionManager collectionManager;
    private Timeline refreshTimeline;
    private ContextMenu filterMenu;
    private Employee currentUser;

    @FXML
    private Button showRelatedCollections;

    @FXML
    private Button completeTripButton;

    @FXML
    private Button filterButton;

    @FXML
    private Button editTripButton;

    @FXML
    private Button deleteTripButton;

    @FXML
    private Button deleteTripPermanentlyButton;

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

    @FXML
    private CheckBox showActiveCheckBox;

    @FXML
    private CheckBox showCancelledCheckBox;

    @FXML
    private CheckBox showCompletedCheckBox;

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
        postalCodeColumn.setCellValueFactory(
                new PropertyValueFactory<>(FIELD_POSTAL_CODES));
        vehicleColumn.setCellValueFactory(
                new PropertyValueFactory<>(FIELD_VEHICLE_MODEL));
        vehicleCapacityColumn.setCellValueFactory(
                new PropertyValueFactory<>(FIELD_VEHICLE_CAPACITY));
        operatorsColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_OPERATORS));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_DEPARTURE));
        returnColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_RETURN));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_STATUS));

        showActiveCheckBox.setSelected(true);
        showCancelledCheckBox.setSelected(false);
        showCompletedCheckBox.setSelected(false);

        showActiveCheckBox.selectedProperty().addListener((o, ov, nv) -> refresh());
        showCancelledCheckBox.selectedProperty().addListener((o, ov, nv) -> refresh());
        showCompletedCheckBox.selectedProperty().addListener((o, ov, nv) -> refresh());

        searchField.textProperty().addListener((obs, oldText, newText) -> handleSearch());
        tripTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean rowSelected = newVal != null;
                    editTripButton.setDisable(!rowSelected);
                    deleteTripButton.setDisable(!rowSelected);
                    deleteTripPermanentlyButton.setDisable(!rowSelected);
                });
        tripTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    boolean rowSelected = newVal != null;
                    if (rowSelected) {
                        String status = newVal.getStatus();
                        boolean isActive = "ACTIVE".equalsIgnoreCase(status);
                        editTripButton.setDisable(!isActive);
                        deleteTripButton.setDisable(!isActive);
                        deleteTripPermanentlyButton.setDisable(!isActive);
                        completeTripButton.setDisable(!isActive);

                        boolean isNotCancelled = !"CANCELED".equalsIgnoreCase(status)
                                && !"CANCELLED".equalsIgnoreCase(status);
                        showRelatedCollections.setDisable(!isNotCancelled);

                    } else {
                        editTripButton.setDisable(true);
                        deleteTripButton.setDisable(true);
                        deleteTripPermanentlyButton.setDisable(true);
                        completeTripButton.setDisable(true);
                        showRelatedCollections.setDisable(true);
                    }
                });

        currentUser = AppContext.getCurrentAccount().getEmployee();
        boolean isAllowedToCompleteTrip = currentUser.getRole() == Employee.Role.ADMINISTRATOR
                || currentUser.getRole() == Employee.Role.OPERATOR;
        completeTripButton.setVisible(isAllowedToCompleteTrip);

        this.notificationService = AppContext.getServiceFactory().getNotificationService();
    }

    public void initData() {
        loadTrips();
    }

    public void refresh() {
        handleSearch();
    }

    @Override
    public void startAutoRefresh() {
        if (refreshTimeline != null || tripManager == null) {
            return;
        }
        refreshTimeline = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_SECONDS),
                event -> loadTrips()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    @Override
    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
    }

    /**
     * Loads trips visible to the current user and updates the trip table.
     * Operators see only their assigned trips, while admins and office workers see
     * all trips.
     */
    public void loadTrips() {
        List<Trip> trips = tripManager.getTripsForCurrentUser(currentUser);
        allTrips.clear();
        for (Trip trip : trips) {
            allTrips.add(new TripRow(trip));
        }
        handleSearch();
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
        }
    }

    @FXML
    private void handleDeleteTripPermanently() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to delete permanently.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Permanent Deletion",
                "Are you sure you want to permanently delete this trip? Recurring "
                        + "collections will be rescheduled to the next available date.",
                AppContext.getOwner());
        if (!confirmed) {
            return;
        }

        Optional<Trip> tripOpt = tripManager.getTripById(selected.getIdAsInt());
        if (tripOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected trip could not be found.", AppContext.getOwner());
            return;
        }

        Trip trip = tripOpt.get();
        boolean success = tripManager.softDeleteAndRescheduleNextCollection(trip);

        if (success) {
            try {
                List<String> recipients = extractCustomerEmails(trip);
                if (!recipients.isEmpty() && notificationService != null) {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String formattedDeparture = trip.getDepartureTime().format(fmt);

                    String subject = "Trip cancellation #" + trip.getTripId();
                    String body = "Dear customer,\n\n"
                            + "We inform you that trip #" + trip.getTripId()
                            + " (ZIP " + trip.getPostalCode() + ", departure " + formattedDeparture + ") "
                            + "has been cancelled. Any recurring collections have been rescheduled to the next available date.\n\n"
                            + "For assistance, please reply to this email.\n"
                            + "Best regards,\nWasteMaster";

                    notificationService.notifyTripCancellation(recipients, subject, body);
                }
            } catch (Exception ex) {
                DialogUtils.showError("Notification Warning",
                        "Trip deleted, but customers could not be notified:\n" + ex.getMessage(),
                        AppContext.getOwner());
            }

            loadTrips();
            DialogUtils.showSuccess("Trip permanently deleted and recurring collections rescheduled.",
                    AppContext.getOwner());
        } else {
            DialogUtils.showError("Deletion Failed",
                    "Unable to delete the selected trip.", AppContext.getOwner());
        }
    }

    private List<String> extractCustomerEmails(Trip trip) {
        if (trip.getCollections() == null)
            return List.of();
        Set<String> unique = trip.getCollections().stream()
                .map(c -> c.getCustomer())
                .filter(cu -> cu != null && cu.getEmail() != null && !cu.getEmail().isBlank())
                .map(cu -> cu.getEmail().trim())
                .collect(Collectors.toSet());
        return List.copyOf(unique);
    }

    @FXML
    private void handleDeleteTrip() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to delete.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Deletion",
                "Are you sure you want to delete this trip?",
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
        if (success) {
            trip.getCollections().clear();
            loadTrips();
            DialogUtils.showSuccess("Trip deleted.", AppContext.getOwner());
        } else {
            DialogUtils.showError("Deletion Failed",
                    "Unable to delete the selected trip.", AppContext.getOwner());
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
                        ctrl.setTripManager(tripManager);
                        ctrl.setVehicleManager(vehicleManager);
                        ctrl.initData();
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

        ObservableList<TripRow> base = FXCollections.observableArrayList(allTrips);
        ObservableList<TripRow> byStatus = FXCollections.observableArrayList();
        for (TripRow row : base) {
            if (shouldShowByStatus(row.getStatus())) {
                byStatus.add(row);
            }
        }
        if (query.isEmpty()) {
            tripTable.setItems(byStatus);
            return;
        }

        ObservableList<TripRow> filtered = FXCollections.observableArrayList();
        for (TripRow row : byStatus) {
            if (matchesQuery(row, query)) {
                filtered.add(row);
            }
        }
        tripTable.setItems(filtered);
    }

    private boolean shouldShowByStatus(String status) {
        if (status == null) {
            return false;
        }
        String s = status.toUpperCase();

        boolean isActive = "ACTIVE".equals(s);
        boolean isCancelled = "CANCELED".equals(s) || "CANCELLED".equals(s);
        boolean isCompleted = "COMPLETED".equals(s);

        return (isActive && showActiveCheckBox.isSelected())
                || (isCancelled && showCancelledCheckBox.isSelected())
                || (isCompleted && showCompletedCheckBox.isSelected());
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
        activeFilters.addAll(FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL,
                FIELD_VEHICLE_CAPACITY,
                FIELD_OPERATORS, FIELD_STATUS);
        showActiveCheckBox.setSelected(true);
        showCancelledCheckBox.setSelected(false);
        showCompletedCheckBox.setSelected(false);
        loadTrips();
    }

    @FXML
    private void showFilterMenu(final javafx.scene.input.MouseEvent event) {
        if (filterMenu != null && filterMenu.isShowing()) {
            filterMenu.hide();
            return;
        }

        filterMenu = new ContextMenu();
        String[] fields = { FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE_MODEL,
                FIELD_VEHICLE_CAPACITY,
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

    @FXML
    private void handleCompleteTrip() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to complete.", AppContext.getOwner());
            return;
        }

        boolean confirmed = DialogUtils.showConfirmationDialog(
                "Confirm Completion",
                "Are you sure you want to mark this trip as completed?",
                AppContext.getOwner());
        if (!confirmed) {
            return;
        }

        Optional<Trip> tripOpt = tripManager.getTripById(selected.getIdAsInt());
        if (tripOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected trip could not be found.", AppContext.getOwner());
            return;
        }

        Trip trip = tripOpt.get();
        boolean success = tripManager.setTripAsCompleted(trip);
        if (success) {
            loadTrips();
        } else {
            DialogUtils.showError("Completion Failed",
                    "Unable to complete the selected trip.", AppContext.getOwner());
        }

    }

    @FXML
    private void handleShowRelatedCollections() {
        TripRow selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection",
                    "Please select a trip to view its collections.", AppContext.getOwner());
            return;
        }

        Optional<Trip> tripOpt = tripManager.getTripById(selected.getIdAsInt());
        if (tripOpt.isEmpty()) {
            DialogUtils.showError("Not Found",
                    "The selected trip could not be found.", AppContext.getOwner());
            return;
        }

        Trip trip = tripOpt.get();
        List<Collection> collections = tripManager.getCollectionsByTrip(trip);

        try {
            MainLayoutController.getInstance().setPageTitle("Trip Related Collections");
            CollectionController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/collection/CollectionView.fxml");
            controller.setPreviousPage("TRIP");
            controller.setTripManager(tripManager);
            controller.setVehicleManager(vehicleManager);
            controller.setCollectionManager(collectionManager);
            controller.setCollections(collections);

        } catch (Exception e) {
            DialogUtils.showError("Navigation error",
                    "Could not load Associated Collections view.", AppContext.getOwner());
            e.printStackTrace();
        }
    }
}
