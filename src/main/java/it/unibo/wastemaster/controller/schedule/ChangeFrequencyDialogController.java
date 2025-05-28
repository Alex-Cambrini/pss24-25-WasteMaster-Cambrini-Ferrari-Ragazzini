package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Controller for the frequency change dialog in schedule editing.
 * Allows the user to select a new frequency for a recurring schedule.
 */
public final class ChangeFrequencyDialogController {

    @FXML
    private ComboBox<Frequency> frequencyComboBox;

    @SuppressWarnings("unused")
    private RecurringSchedule schedule;

    private Frequency selectedFrequency;

    /**
     * Sets the schedule to be potentially used during frequency change.
     *
     * @param schedule the recurring schedule object
     */
    public void setSchedule(final RecurringSchedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Sets the current frequency to be selected in the combo box.
     *
     * @param currentFrequency the current frequency value
     */
    public void setCurrentFrequency(final Frequency currentFrequency) {
        frequencyComboBox.getSelectionModel().select(currentFrequency);
    }

    /**
     * Populates the frequency combo box with the available values.
     * Selects the first one by default if not empty.
     *
     * @param frequencies list of available frequencies
     */
    public void setFrequencies(final List<Frequency> frequencies) {
        frequencyComboBox.getItems().setAll(frequencies);
        if (!frequencies.isEmpty()) {
            frequencyComboBox.getSelectionModel().select(0);
        }
    }

    /**
     * Returns the frequency selected by the user.
     *
     * @return the new frequency selected
     */
    public Frequency getSelectedFrequency() {
        return selectedFrequency;
    }

    /**
     * Handles the save action, storing the selected frequency and
     * closing the dialog.
     */
    @FXML
    private void handleSave() {
        selectedFrequency = frequencyComboBox.getSelectionModel().getSelectedItem();
        frequencyComboBox.getScene().getWindow().hide();
    }
}
