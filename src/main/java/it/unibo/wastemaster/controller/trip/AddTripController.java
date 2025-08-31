package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.service.TripManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AddTripController {

    @FXML private TextField postalCodesField;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private ListView<Employee> operatorsList;
    @FXML private DatePicker departureDate;
    @FXML private DatePicker returnDate;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private TripManager tripManager;
    private TripController tripController;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setTripController(TripController tripController) {
        this.tripController = tripController;
    }


}