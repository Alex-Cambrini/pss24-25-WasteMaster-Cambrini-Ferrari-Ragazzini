package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Waste;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * Controller for adding a new waste type.
 */
public final class AddWasteController {

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox recyclableCheckBox;

    @FXML
    private CheckBox dangerousCheckBox;

    /**
     * Saves the waste entry if valid.
     *
     * @param event the action event
     */
    @FXML
    private void handleSaveWaste(final ActionEvent event) {
        try {
            String name = nameField.getText().trim();
            boolean recyclable = recyclableCheckBox.isSelected();
            boolean dangerous = dangerousCheckBox.isSelected();

            if (name.isBlank()) {
                throw new IllegalArgumentException("Waste name cannot be empty.");
            }

            Waste waste = new Waste(name, recyclable, dangerous);
            AppContext.getWasteManager().addWaste(waste);

            DialogUtils.showSuccess("Waste saved successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);
        } catch (IllegalArgumentException e) {
            DialogUtils.showError(
                "Validation error", e.getMessage(), AppContext.getOwner());
        } catch (Exception e) {
            DialogUtils.showError(
                "Unexpected error", e.getMessage(), AppContext.getOwner()
            );
        }
    }

    /**
     * Cancels the waste creation and closes the modal.
     *
     * @param event the action event
     */
    @FXML
    private void handleAbortWasteCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
