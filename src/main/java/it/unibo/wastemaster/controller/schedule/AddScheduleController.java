package it.unibo.wastemaster.controller.schedule;

import java.time.LocalDate;
import java.util.List;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

public class AddScheduleController {

    private ScheduleController scheduleController;
    private boolean isRecurring;

    @FXML
    private ToggleGroup scheduleGroup;

    @FXML
    private ComboBox<Frequency> frequencyComboBox;

    @FXML
    private ComboBox<Waste> wasteComboBox;

    @FXML
    private Label wasteField;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField customerField;

    @FXML
    private Label locationField;

    @FXML
    private DatePicker datePicker;

    private List<Customer> allCustomers;
    private ContextMenu suggestionsMenu = new ContextMenu();

    @FXML
    public void initialize() {
        frequencyComboBox.setItems(FXCollections.observableArrayList(Frequency.values()));
        wasteComboBox.setItems(FXCollections.observableArrayList(AppContext.wasteDAO.findAll()));
        allCustomers = AppContext.customerDAO.findAll();

        // Converter per waste
        wasteComboBox.setConverter(new StringConverter<Waste>() {
            @Override
            public String toString(Waste waste) {
                return waste != null ? waste.getWasteName() : "";
            }

            @Override
            public Waste fromString(String string) {
                return null;
            }
        });

        // Listener per selezione waste
        wasteComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                wasteField.setText(newVal.toString());
            } else {
                wasteField.setText("No Waste selected.");
            }
        });

        // Autocomplete dinamico per customer
        customerField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                suggestionsMenu.hide();
                return;
            }

            List<Customer> matches = allCustomers.stream()
                    .filter(c -> (c.getName() + " " + c.getSurname()).toLowerCase().contains(newText.toLowerCase()))
                    .toList();

            if (matches.isEmpty()) {
                suggestionsMenu.hide();
                return;
            }

            suggestionsMenu.getItems().clear();
            for (Customer customer : matches) {
                MenuItem item = new MenuItem(customer.getName() + " " + customer.getSurname());
                item.setOnAction(e -> {
                    customerField.setText(customer.getName() + " " + customer.getSurname());
                    locationField.setText(customer.getLocation().toString());
                    suggestionsMenu.hide();
                });
                suggestionsMenu.getItems().add(item);
            }

            if (!suggestionsMenu.isShowing()) {
                suggestionsMenu.show(customerField, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });

        // Gestione radio buttons per tipo di pianificazione
        scheduleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                RadioButton selected = (RadioButton) newVal;
                isRecurring = selected.getText().equalsIgnoreCase("Recurring");
                frequencyComboBox.setDisable(!isRecurring);
                dateLabel.setText(isRecurring ? "Start Date" : "Pickup Date");
            }
        });
    }

    public void setScheduleController(ScheduleController controller) {
        this.scheduleController = controller;
    }

    @FXML
    public void handleSaveSchedule(ActionEvent event) {
        try {
            Waste selectedWaste = wasteComboBox.getValue();
            String customerInput = customerField.getText();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedWaste == null)
                throw new IllegalArgumentException("- Please select a waste type");

            if (customerInput.isBlank())
                throw new IllegalArgumentException("- Please select a customer");

            Customer selectedCustomer = allCustomers.stream()
                    .filter(c -> (c.getName() + " " + c.getSurname()).equalsIgnoreCase(customerInput.trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("- No matching customer found"));

            if (selectedDate == null)
                throw new IllegalArgumentException("- Please select a date");

            if (isRecurring) {
                Frequency selectedFrequency = frequencyComboBox.getValue();
                if (selectedFrequency == null)
                    throw new IllegalArgumentException("- Please select a frequency");

                AppContext.recurringScheduleManager.createRecurringSchedule(selectedCustomer, selectedWaste,
                        selectedDate, selectedFrequency);
            } else {
                AppContext.oneTimeScheduleManager.createOneTimeSchedule(selectedCustomer, selectedWaste, selectedDate);
            }

            DialogUtils.showSuccess("Schedule saved successfully.");
            scheduleController.returnToScheduleView();

        } catch (Exception e) {
            DialogUtils.showError("Validation error", e.getMessage());
        }
    }

    @FXML
    private void handleAbortScheduleCreation(ActionEvent event) {
        scheduleController.returnToScheduleView();
    }
}
