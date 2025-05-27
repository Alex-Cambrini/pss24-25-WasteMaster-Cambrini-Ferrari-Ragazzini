package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Waste;

import static it.unibo.wastemaster.controller.utils.DialogUtils.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.time.DayOfWeek;

public class AddProgramController {

    @FXML
    private ComboBox<DayOfWeek> dayOfWeekComboBox;

    private Waste selectedWaste;

    @FXML
    public void initialize() {
        dayOfWeekComboBox.getItems().setAll(DayOfWeek.values());
        dayOfWeekComboBox.getSelectionModel().selectFirst();
    }

    public void setWaste(Waste waste) {
        this.selectedWaste = waste;
    }

    @FXML
    private void handleSaveProgram(ActionEvent event) {
        try {
            DayOfWeek selectedDay = dayOfWeekComboBox.getValue();

            if (selectedWaste == null || selectedDay == null) {
                throw new IllegalArgumentException("Invalid input.");
            }

            AppContext.wasteScheduleManager.setupCollectionRoutine(selectedWaste, selectedDay);

            showSuccess("Program saved successfully.", AppContext.getOwner());
            closeModal(event);
        } catch (IllegalArgumentException e) {
            showError("Validation error", e.getMessage(), AppContext.getOwner());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unexpected error", e.getMessage(), AppContext.getOwner());
        }
    }

    @FXML
    private void handleAbortProgramCreation(ActionEvent event) {
        closeModal(event);
    }
}
