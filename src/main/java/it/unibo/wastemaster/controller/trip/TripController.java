package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.service.TripManager;
import it.unibo.wastemaster.viewmodels.TripRow;
import java.util.List;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 * Controller for managing the trips view, including search, filters and CRUD operations.
 */
public final class TripController {

    private static final String FIELD_ID = "id";
    private static final String FIELD_POSTAL_CODES = "postalCodes";
    private static final String FIELD_VEHICLE = "vehicle";
    private static final String FIELD_OPERATORS = "operators";
    private static final String FIELD_DEPARTURE = "departure";
    private static final String FIELD_RETURN = "returnTime";
    private static final String FIELD_STATUS = "status";
    private static final String NAVIGATION_ERROR = "Navigation error";
    private static final int REFRESH_SECONDS = 30;

    private final ObservableList<TripRow> allTrips = FXCollections.observableArrayList();
    private final ObservableList<String> activeFilters = FXCollections.observableArrayList(
            FIELD_ID, FIELD_POSTAL_CODES, FIELD_VEHICLE, FIELD_OPERATORS, FIELD_STATUS
    );
    private TripManager tripManager;
    private Timeline refreshTimeline;
    private ContextMenu filterMenu;

    @FXML private Button filterButton;
    @FXML private Button addTripButton;
    @FXML private Button editTripButton;
    @FXML private Button deleteTripButton;
    @FXML private TextField searchField;
    @FXML private TableView<TripRow> tripTable;
    @FXML private TableColumn<TripRow, String> idColumn;
    @FXML private TableColumn<TripRow, String> postalCodesColumn;
    @FXML private TableColumn<TripRow, String> vehicleColumn;
    @FXML private TableColumn<TripRow, String> operatorsColumn;
    @FXML private TableColumn<TripRow, String> departureColumn;
    @FXML private TableColumn<TripRow, String> returnColumn;
    @FXML private TableColumn<TripRow, String> statusColumn;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    /**
     * Initializes the trip view with columns, search and auto-refresh logic.
     */
    @FXML
    public void initialize() {
        tripManager = AppContext.getServiceFactory().getTripManager();

        idColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_ID));
        postalCodesColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_POSTAL_CODES));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>(FIELD_VEHICLE));
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

        // Carica i dati all'avvio
        loadTrips();
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

}