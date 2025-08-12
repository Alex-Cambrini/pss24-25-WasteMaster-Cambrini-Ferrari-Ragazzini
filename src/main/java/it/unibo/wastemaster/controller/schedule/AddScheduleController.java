package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.domain.model.Customer;
import it.unibo.wastemaster.domain.model.RecurringSchedule.Frequency;
import it.unibo.wastemaster.domain.model.Waste;
import it.unibo.wastemaster.domain.service.CustomerManager;
import it.unibo.wastemaster.domain.service.OneTimeScheduleManager;
import it.unibo.wastemaster.domain.service.RecurringScheduleManager;
import it.unibo.wastemaster.domain.service.WasteManager;
import java.time.LocalDate;
import java.util.List;
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

/**
 * Controller for the Add Schedule modal form. Manages input validation and scheduling
 * creation logic.
 */
public final class AddScheduleController {

    private final ContextMenu suggestionsMenu = new ContextMenu();
    private boolean isRecurring;

    @FXML
    private ToggleGroup scheduleGroup;

    @FXML
    private ComboBox<Frequency> frequencyComboBox;

    @FXML
    private ComboBox<Waste> wasteComboBox;

    @FXML
    private Label wasteDetailsInfo;

    @FXML
    private Label wasteDetailsTitle;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField customerField;

    @FXML
    private Label locationField;

    @FXML
    private DatePicker datePicker;
    private CustomerManager customerManager;
    private OneTimeScheduleManager oneTimeScheduleManager;
    private RecurringScheduleManager recurringScheduleManager;
    private WasteManager wasteManager;
    private List<Customer> allCustomers;

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setOneTimeScheduleManager(OneTimeScheduleManager oneTimeScheduleManager) {
        this.oneTimeScheduleManager = oneTimeScheduleManager;
    }

    public void setRecurringScheduleManager(
            RecurringScheduleManager recurringScheduleManager) {
        this.recurringScheduleManager = recurringScheduleManager;
    }

    public void setWasteManager(WasteManager wasteManager) {
        this.wasteManager = wasteManager;
    }

    /**
     * Sets up basic UI components and listeners that do not depend on external managers.
     */
    @FXML
    public void initialize() {
        setupScheduleTypeToggle();
    }

    /**
     * Initializes data and UI components that depend on injected managers.
     * This method must be called after all required managers have been set.
     */
    public void initData() {
        System.out.println("[DEBUG] initData addSchedule Controller called");
        setupFrequencyComboBox();
        setupCustomerAutocomplete();
        setupWasteComboBox();
    }

    private void setupCustomerAutocomplete() {
        allCustomers = customerManager.getAllCustomers();
        System.out.println("[DEBUG] Loaded customers: " + allCustomers.size());

        customerField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                suggestionsMenu.hide();
                return;
            }

            List<Customer> matches =
                    allCustomers.stream()
                            .filter(c -> (c.getName() + " " + c.getSurname())
                                    .toLowerCase().contains(newText.toLowerCase()))
                            .toList();

            if (matches.isEmpty()) {
                suggestionsMenu.hide();
                return;
            }

            suggestionsMenu.getItems().clear();
            for (Customer customer : matches) {
                MenuItem item =
                        new MenuItem(customer.getName() + " " + customer.getSurname());
                item.setOnAction(e -> {
                    customerField
                            .setText(customer.getName() + " " + customer.getSurname());
                    locationField.setText(customer.getLocation().toString());
                    suggestionsMenu.hide();
                });
                suggestionsMenu.getItems().add(item);
            }

            if (!suggestionsMenu.isShowing()) {
                suggestionsMenu.show(customerField, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });
    }

    private void setupWasteComboBox() {
        List<Waste> wasteList = wasteManager.getActiveWastes();
        System.out.println("[DEBUG] Loaded wastes: " + wasteList.size());

        if (wasteList.isEmpty()) {
            wasteComboBox.setDisable(true);
            wasteDetailsTitle.setText("No Waste available");
        } else {
            wasteComboBox.setItems(FXCollections.observableArrayList(wasteList));
            wasteDetailsInfo.setText("No Waste selected.");
            wasteDetailsTitle.setText("Waste Details:");

            wasteComboBox.valueProperty().addListener((obs, oldWaste, newWaste) -> {
                if (newWaste != null) {
                    wasteDetailsInfo.setText(String.format(
                            "Name: %s%nRecyclable: %s%nDangerous: %s",
                            newWaste.getWasteName(),
                            newWaste.getIsRecyclable() ? "Yes" : "No",
                            newWaste.getIsDangerous() ? "Yes" : "No"
                    ));
                } else {
                    wasteDetailsInfo.setText("No Waste selected.");
                }
            });
        }

        wasteComboBox.setConverter(new StringConverter<Waste>() {
            @Override
            public String toString(final Waste waste) {
                return waste != null ? waste.getWasteName() : "Select Waste";
            }

            @Override
            public Waste fromString(final String string) {
                return null;
            }
        });
    }

    private void setupScheduleTypeToggle() {
        scheduleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                final RadioButton selected = (RadioButton) newVal;
                isRecurring = selected.getText().equalsIgnoreCase("Recurring");
                frequencyComboBox.setDisable(!isRecurring);
                dateLabel.setText(isRecurring ? "Start Date" : "Pickup Date");
            }
        });
    }

    private void setupFrequencyComboBox() {
        frequencyComboBox.setItems(FXCollections.observableArrayList(Frequency.values()));
        frequencyComboBox.setConverter(new StringConverter<Frequency>() {
            @Override
            public String toString(Frequency freq) {
                return freq != null ? freq.name() : "";
            }
            @Override
            public Frequency fromString(String string) {
                return null;
            }
        });
        frequencyComboBox.setDisable(true);
    }

    /**
     * Saves the schedule with provided data.
     *
     * @param event the action event from the save button
     */
    @FXML
    public void handleSaveSchedule(final ActionEvent event) {
        try {
            final Waste selectedWaste = wasteComboBox.getValue();
            final String customerInput = customerField.getText();
            final LocalDate selectedDate = datePicker.getValue();

            if (selectedWaste == null) {
                throw new IllegalArgumentException("- Please select a waste type");
            }

            if (customerInput.isBlank()) {
                throw new IllegalArgumentException("- Please select a customer");
            }

            final Customer selectedCustomer = allCustomers.stream()
                    .filter(c -> (c.getName() + " " + c.getSurname())
                            .equalsIgnoreCase(customerInput.trim()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(
                            "- No matching customer found"));

            if (selectedDate == null) {
                throw new IllegalArgumentException("- Please select a date");
            }

            if (isRecurring) {
                final Frequency selectedFrequency = frequencyComboBox.getValue();
                recurringScheduleManager.createRecurringSchedule(
                        selectedCustomer, selectedWaste, selectedDate, selectedFrequency);
            } else {
                oneTimeScheduleManager.createOneTimeSchedule(
                        selectedCustomer, selectedWaste, selectedDate);
            }

            DialogUtils.showSuccess("Schedule saved successfully.",
                    AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (final Exception e) {
            DialogUtils.showError("Validation error", e.getMessage(),
                    AppContext.getOwner());
        }
    }

    /**
     * Cancels and closes the schedule creation modal.
     *
     * @param event the cancel button event
     */
    @FXML
    private void handleAbortScheduleCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
