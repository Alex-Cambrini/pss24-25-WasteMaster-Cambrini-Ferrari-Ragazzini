package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import it.unibo.wastemaster.core.models.Waste;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class AddScheduleController {

    private ScheduleController scheduleController;

    @FXML
    private ComboBox<Frequency> frequencyComboBox;

    @FXML
    private ComboBox<Waste> wasteComboBox;

    @FXML
    private Label wasteField;

    @FXML
    public void initialize() {
        frequencyComboBox.setItems(FXCollections.observableArrayList(Frequency.values()));

		//TO UPDATE WITH SERVICES METHOD 
        wasteComboBox.setItems(FXCollections.observableArrayList(AppContext.wasteDAO.findAll()));

        wasteComboBox.setConverter(new StringConverter<Waste>() {
            @Override
            public String toString(Waste waste) {
                return waste != null ? waste.getType().name() : "";
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
    }

    public void setScheduleController(ScheduleController controller) {
        this.scheduleController = controller;
    }

    @FXML
    public void handleSaveSchedule(ActionEvent event) {
        // TODO: implementa salvataggio
    }

    @FXML
    private void handleAbortScheduleCreation(ActionEvent event) {
        scheduleController.returnToScheduleView();
    }
}
