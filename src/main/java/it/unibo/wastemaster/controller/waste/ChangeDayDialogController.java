package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.domain.model.WasteSchedule;
import java.time.DayOfWeek;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;


/**
 * Controller for changing the collection day of a waste schedule.
 */
public final class ChangeDayDialogController {

    private WasteSchedule schedule;

    @FXML
    private ComboBox<DayOfWeek> dayOfWeekComboBox;

    /**
     * Initializes the combo box with the current day selected.
     *
     * @param currentDay the current day to be preselected
     */
    public void setCurrentDay(final DayOfWeek currentDay) {
        dayOfWeekComboBox.getItems().setAll(Arrays.asList(DayOfWeek.values()));
        dayOfWeekComboBox.getSelectionModel().select(currentDay);
    }

    /**
     * Returns the day selected by the user.
     *
     * @return the selected day
     */
    public DayOfWeek getSelectedDay() {
        return dayOfWeekComboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Closes the dialog after selection.
     */
    @FXML
    private void handleSave() {
        if (schedule == null) {
            throw new IllegalStateException("Schedule must be set before saving.");
        }
        dayOfWeekComboBox.getScene().getWindow().hide();
    }


    /**
     * Sets the waste schedule for this controller.
     *
     * @param schedule the WasteSchedule to set
     */
    public void setSchedule(final WasteSchedule schedule) {
        this.schedule = schedule;
    }
}
