package it.unibo.wastemaster.controller.schedule;

import java.util.List;

import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule.Frequency;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ChangeFrequencyDialogController {

    @FXML
    private ComboBox<Frequency> frequencyComboBox;

    private RecurringSchedule schedule;
    private Frequency selectedFrequency;

    public void setSchedule(RecurringSchedule schedule) {
        this.schedule = schedule;
    }

    public void setCurrentFrequency(Frequency currentFrequency) {
    frequencyComboBox.getSelectionModel().select(currentFrequency);
}

    public void setFrequencies(List<Frequency> frequencies) {
        frequencyComboBox.getItems().setAll(frequencies);
        if (!frequencies.isEmpty()) {
            frequencyComboBox.getSelectionModel().select(0);
        }
    }

    public Frequency getSelectedFrequency() {
        return frequencyComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleSave() {
        selectedFrequency = frequencyComboBox.getSelectionModel().getSelectedItem();
        frequencyComboBox.getScene().getWindow().hide();
    }

}
