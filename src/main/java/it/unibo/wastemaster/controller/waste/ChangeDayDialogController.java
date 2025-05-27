package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.core.models.WasteSchedule;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.time.DayOfWeek;
import java.util.Arrays;

public class ChangeDayDialogController {

    @FXML
    private ComboBox<DayOfWeek> dayOfWeekComboBox;

    private WasteSchedule schedule;

    public void setSchedule(WasteSchedule schedule) {
        this.schedule = schedule;
    }

    public void setCurrentDay(DayOfWeek currentDay) {
        dayOfWeekComboBox.getItems().setAll(Arrays.asList(DayOfWeek.values()));
        dayOfWeekComboBox.getSelectionModel().select(currentDay);
    }

    public DayOfWeek getSelectedDay() {
        return dayOfWeekComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleSave() {
        dayOfWeekComboBox.getScene().getWindow().hide();
    }
}