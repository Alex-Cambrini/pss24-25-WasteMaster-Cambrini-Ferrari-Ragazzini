package it.unibo.wastemaster.controller.collection;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.schedule.ScheduleController;
import it.unibo.wastemaster.controller.trip.TripController;
import it.unibo.wastemaster.domain.model.Collection;
import it.unibo.wastemaster.domain.service.CollectionManager;
import it.unibo.wastemaster.domain.service.OneTimeScheduleManager;
import it.unibo.wastemaster.domain.service.RecurringScheduleManager;
import it.unibo.wastemaster.domain.service.ScheduleManager;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.domain.service.VehicleManager;
import it.unibo.wastemaster.presentationdto.CollectionRow;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for displaying and filtering waste collections in the view.
 */
public final class CollectionController {

    private final ObservableList<CollectionRow> allSchedules =
            FXCollections.observableArrayList();

    @FXML
    private Label totalLabel;

    @FXML
    private Label completedLabel;

    @FXML
    private Label cancelledLabel;

    @FXML
    private Label activeLabel;

    @FXML
    private TableView<CollectionRow> collectionTable;

    @FXML
    private TableColumn<CollectionRow, String> wasteNameColumn;

    @FXML
    private TableColumn<CollectionRow, String> dateColumn;

    @FXML
    private TableColumn<CollectionRow, String> zoneColumn;

    @FXML
    private TableColumn<CollectionRow, String> statusColumn;

    @FXML
    private TableColumn<CollectionRow, String> customerColumn;

    @FXML
    private CheckBox showCompletedCheckBox;

    @FXML
    private CheckBox showCancelledCheckBox;

    @FXML
    private CheckBox showActiveCheckBox;
    private CollectionManager collectionManager;
    private OneTimeScheduleManager oneTimeScheduleManager;
    private RecurringScheduleManager recurringScheduleManager;
    private ScheduleManager scheduleManager;
    private TripManager tripManager;
    private VehicleManager vehicleManager;
    private String previousPage;
    private List<Collection> collections;

    /**
     * Sets the previous page identifier for navigation.
     *
     * @param previousPage the previous page identifier
     */
    public void setPreviousPage(final String previousPage) {
        this.previousPage = previousPage;
    }

    /**
     * Sets the schedule manager.
     *
     * @param scheduleManager the schedule manager to set
     */
    public void setScheduleManager(final ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    /**
     * Sets the recurring schedule manager.
     *
     * @param recurringScheduleManager the recurring schedule manager to set
     */
    public void setRecurringScheduleManager(
            final RecurringScheduleManager recurringScheduleManager) {
        this.recurringScheduleManager = recurringScheduleManager;
    }

    /**
     * Sets the one-time schedule manager.
     *
     * @param oneTimeScheduleManager the one-time schedule manager to set
     */
    public void setOneTimeScheduleManager(
            final OneTimeScheduleManager oneTimeScheduleManager) {
        this.oneTimeScheduleManager = oneTimeScheduleManager;
    }

    /**
     * Sets the trip manager.
     *
     * @param tripManager the trip manager to set
     */
    public void setTripManager(final TripManager tripManager) {
        this.tripManager = tripManager;
    }

    /**
     * Sets the vehicle manager.
     *
     * @param vehicleManager the vehicle manager to set
     */
    public void setVehicleManager(final VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
    }

    /**
     * Sets the collection manager.
     *
     * @param collectionManager the collection manager to set
     */
    public void setCollectionManager(final CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Sets the list of collections and refreshes the table and statistics.
     *
     * @param collections the list of collection entities to display
     */
    public void setCollections(final List<Collection> collections) {
        this.collections = collections;
        refresh();
    }

    /**
     * Refreshes the collection table and statistics.
     */
    public void refresh() {
        loadCollections();
        updateStatusCounts();
        applyFilters();
    }

    /**
     * Initializes the view, column bindings, and filter checkboxes.
     */
    @FXML
    public void initialize() {
        wasteNameColumn.setCellValueFactory(new PropertyValueFactory<>("wasteName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("collectionDate"));
        zoneColumn.setCellValueFactory(new PropertyValueFactory<>("zone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        collectionTable.setItems(allSchedules);

        showCompletedCheckBox.setSelected(true);
        showCancelledCheckBox.setSelected(true);
        showActiveCheckBox.setSelected(true);

        showCompletedCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
        showCancelledCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
        showActiveCheckBox.selectedProperty()
                .addListener((obs, oldVal, newVal) -> refresh());
    }

    /**
     * Handles the back navigation based on the previous page.
     */
    @FXML
    private void handleBack() {
        if ("SCHEDULE".equals(previousPage)) {
            MainLayoutController.getInstance().setPageTitle("Schedule Management");
            ScheduleController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/schedule/ScheduleView.fxml");
            controller.setCollectionManager(collectionManager);
            controller.setScheduleManager(scheduleManager);
            controller.setOneTimeScheduleManager(oneTimeScheduleManager);
            controller.setRecurringScheduleManager(recurringScheduleManager);
            controller.initData();
        } else if ("TRIP".equals(previousPage)) {
            MainLayoutController.getInstance().setPageTitle("Trip Management");
            TripController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/trip/TripView.fxml");
            controller.setTripManager(tripManager);
            controller.setVehicleManager(vehicleManager);
            controller.setCollectionManager(collectionManager);
            controller.initData();
        }
    }

    private void loadCollections() {
        if (collections == null) {
            return;
        }

        allSchedules.clear();
        for (Collection c : collections) {
            allSchedules.add(new CollectionRow(c));
        }
    }

    private void updateStatusCounts() {
        int total = allSchedules.size();
        int completed = 0;
        int cancelled = 0;
        int active = 0;

        for (CollectionRow c : allSchedules) {
            switch (c.getStatus()) {
                case COMPLETED -> completed++;
                case CANCELLED -> cancelled++;
                case ACTIVE -> active++;
                default -> {

                }
            }
        }

        totalLabel.setText("Total: " + total);
        completedLabel.setText("Completed: " + completed);
        cancelledLabel.setText("Cancelled: " + cancelled);
        activeLabel.setText("Active: " + active);
    }

    private void applyFilters() {
        ObservableList<CollectionRow> filtered = FXCollections.observableArrayList();
        for (CollectionRow row : allSchedules) {
            if (isStatusVisible(row)) {
                filtered.add(row);
            }
        }
        collectionTable.setItems(filtered);
    }

    private boolean isStatusVisible(final CollectionRow row) {
        return switch (row.getStatus()) {
            case COMPLETED -> showCompletedCheckBox.isSelected();
            case CANCELLED -> showCancelledCheckBox.isSelected();
            case ACTIVE -> showActiveCheckBox.isSelected();
            default -> false;
        };
    }
}
