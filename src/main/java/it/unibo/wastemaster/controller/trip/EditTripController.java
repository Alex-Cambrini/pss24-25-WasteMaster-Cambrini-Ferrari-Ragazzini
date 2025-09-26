package it.unibo.wastemaster.controller.trip;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Employee;
import it.unibo.wastemaster.domain.model.Trip;
import it.unibo.wastemaster.domain.model.Vehicle;
import it.unibo.wastemaster.domain.repository.EmployeeRepository;
import it.unibo.wastemaster.domain.repository.VehicleRepository;
import it.unibo.wastemaster.domain.service.TripManager;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;

public final class EditTripController {

    private Trip tripToEdit;
    private TripController tripController;
    private TripManager tripManager;
    private VehicleRepository vehicleRepository;
    private EmployeeRepository employeeRepository;

    @FXML private TextField postalCodeField;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private ListView<Employee> operatorsList;
    @FXML private DatePicker departureDate;
    @FXML private DatePicker returnDate;

    public void setTripManager(TripManager tripManager) {
        this.tripManager = tripManager;
    }

    public void setTripController(TripController tripController) {
        this.tripController = tripController;
    }

    public void setVehicleRepository(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void setTripToEdit(final Trip trip) {
        this.tripToEdit = trip;
        populateFields();
    }

    @FXML
    public void initialize() {
        if (vehicleRepository != null) {
            vehicleCombo.getItems().setAll(vehicleRepository.findAll());
        }
        if (employeeRepository != null) {
            operatorsList.getItems().setAll(employeeRepository.findAllActive());
            operatorsList.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Employee emp, boolean empty) {
                    super.updateItem(emp, empty);
                    if (empty || emp == null) {
                        setText(null);
                    } else {
                        setText(emp.getName() + " " + emp.getSurname() + " (" + emp.getEmail() + ")");
                    }
                }
            });
            operatorsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }

    private void populateFields() {
        if (tripToEdit == null) return;
        postalCodeField.setText(tripToEdit.getPostalCode());
        vehicleCombo.setValue(tripToEdit.getAssignedVehicle());
        operatorsList.getSelectionModel().clearSelection();
        for (Employee op : tripToEdit.getOperators()) {
            operatorsList.getSelectionModel().select(op);
        }
        if (tripToEdit.getDepartureTime() != null) {
            departureDate.setValue(tripToEdit.getDepartureTime().toLocalDate());
        }
        if (tripToEdit.getExpectedReturnTime() != null) {
            returnDate.setValue(tripToEdit.getExpectedReturnTime().toLocalDate());
        }
    }

    @FXML
    private void handleUpdateTrip(final ActionEvent event) {
        try {
            Optional<Trip> originalOpt = tripManager.getTripById(tripToEdit.getTripId());
            if (originalOpt.isEmpty()) {
                DialogUtils.showError("Error", "Trip not found.", AppContext.getOwner());
                return;
            }
            Trip original = originalOpt.get();

            boolean changed = !original.getPostalCode().equals(postalCodeField.getText())
                    || !original.getAssignedVehicle().equals(vehicleCombo.getValue())
                    || !original.getOperators().equals(operatorsList.getSelectionModel().getSelectedItems())
                    || !original.getDepartureTime().toLocalDate().equals(departureDate.getValue())
                    || !original.getExpectedReturnTime().toLocalDate().equals(returnDate.getValue());

            if (!changed) {
                DialogUtils.showError("No changes", "No fields were modified.", AppContext.getOwner());
                return;
            }

            tripToEdit.setPostalCode(postalCodeField.getText());
            tripToEdit.setAssignedVehicle(vehicleCombo.getValue());
            tripToEdit.setOperators(operatorsList.getSelectionModel().getSelectedItems());
            tripToEdit.setDepartureTime(departureDate.getValue().atStartOfDay());
            tripToEdit.setExpectedReturnTime(returnDate.getValue().atStartOfDay());

            tripManager.updateTrip(tripToEdit);

            if (this.tripController != null) {
                this.tripController.loadTrips();
            }

            DialogUtils.showSuccess("Trip updated successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(), AppContext.getOwner());
        }
    }

    @FXML
    private void handleAbortTripEdit(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}