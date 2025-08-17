package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.service.TripManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class TripController {

    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, Integer> idColumn;
    @FXML private TableColumn<Trip, String> capColumn;
    @FXML private TableColumn<Trip, String> vehicleColumn;
    @FXML private TableColumn<Trip, String> operatorsColumn;
    @FXML private TableColumn<Trip, String> departureColumn;
    @FXML private TableColumn<Trip, String> returnColumn;
    @FXML private TableColumn<Trip, String> statusColumn;

    private final TripManager tripManager = /* recupera dal contesto o crea qui */ null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTripId()));
       // capColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCap()));
        vehicleColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAssignedVehicle() != null ? data.getValue().getAssignedVehicle().getPlate() : ""));
        operatorsColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getOperators().stream().map(op -> op.getName() + " " + op.getSurname()).reduce((a, b) -> a + ", " + b).orElse("")));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        departureColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDepartureTime() != null ? data.getValue().getDepartureTime().format(fmt) : ""));
        returnColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getExpectedReturnTime() != null ? data.getValue().getExpectedReturnTime().format(fmt) : ""));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus() != null ? data.getValue().getStatus().toString() : ""));

        onRefresh();
    }

    @FXML
    private void onRefresh() {
    //    tripTable.getItems().setAll(tripManager.getAllTrips());
    }

    @FXML
    private void onDetails() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Mostra dettagli trip (puoi aprire un dialog o una nuova view)
            Alert alert = new Alert(Alert.AlertType.INFORMATION, selected.toString(), ButtonType.OK);
            alert.setHeaderText("Trip Details");
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getStatus() != Trip.TripStatus.CANCELED) {
            tripManager.handleUnexpectedEvent(
                    selected.getTripId(),
                    null, null, null, null, null,
                    Trip.TripStatus.CANCELED
            );
            onRefresh();
        }
    }
}