package it.unibo.wastemaster.controller.schedule;

import java.util.List;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    public void initialize() {
        frequencyComboBox.setItems(FXCollections.observableArrayList(Frequency.values()));

        // TO UPDATE WITH SERVICES METHOD
        wasteComboBox.setItems(FXCollections.observableArrayList(AppContext.wasteDAO.findAll()));

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

        wasteComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                wasteField.setText(newVal.toString());
            } else {
                wasteField.setText("No Waste selected.");
            }
        });

        scheduleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                RadioButton selected = (RadioButton) newVal;
                isRecurring = selected.getText().equals("Recurring");

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
        if (isRecurring) {
            System.out.println("RECURRING");
        } else {
            System.out.println("ONETIME");
        }
    }

    @FXML
    private void handleAbortScheduleCreation(ActionEvent event) {
        scheduleController.returnToScheduleView();
    }
}
