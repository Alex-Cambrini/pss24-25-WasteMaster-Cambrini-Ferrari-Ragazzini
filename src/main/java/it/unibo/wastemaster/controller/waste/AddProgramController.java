package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Waste;
import java.time.DayOfWeek;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Controller for creating a weekly waste collection program.
 */
public final class AddProgramController {

    @FXML
    private ComboBox<DayOfWeek> dayOfWeekComboBox;

    private Waste selectedWaste;

    /**
     * Initializes the controller. Populates the combo box with days of the week.
     */
    @FXML
    public void initialize() {
        dayOfWeekComboBox.getItems().setAll(DayOfWeek.values());
        dayOfWeekComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Sets the waste type for which to configure the program.
     *
     * @param waste the waste entity to associate
     */
    public void setWaste(final Waste waste) {
        this.selectedWaste = waste;
    }

    /**
     * Handles saving the collection program.
     *
     * @param event the save action event
     */
    @FXML
    private void handleSaveProgram(final ActionEvent event) {
        try {
            DayOfWeek selectedDay = dayOfWeekComboBox.getValue();

            if (selectedWaste == null || selectedDay == null) {
                throw new IllegalArgumentException("Invalid input.");
            }

            AppContext.getWasteScheduleManager().setupCollectionRoutine(selectedWaste,
                    selectedDay);

            DialogUtils.showSuccess("Program saved successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);
        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage(),
                    AppContext.getOwner());
        } catch (Exception e) {
            DialogUtils.showError("Unexpected error", e.getMessage(),
                    AppContext.getOwner());
        }
    }

    /**
     * Cancels the program creation and closes the modal.
     *
     * @param event the abort action event
     */
    @FXML
    private void handleAbortProgramCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
