package it.unibo.wastemaster.controller.waste;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Waste;

import static it.unibo.wastemaster.controller.utils.DialogUtils.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class AddWasteController {

	@FXML
	private TextField nameField;

	@FXML
	private CheckBox recyclableCheck;

	@FXML
	private CheckBox dangerousCheck;

	@FXML
	private void handleSaveWaste(ActionEvent event) {
		try {
			String name = nameField.getText().trim();
			boolean recyclable = recyclableCheck.isSelected();
			boolean dangerous = dangerousCheck.isSelected();

			if (name.isBlank()) {
				throw new IllegalArgumentException("Waste name cannot be empty.");
			}

			Waste waste = new Waste(name, recyclable, dangerous);
			AppContext.getWasteManager().addWaste(waste);
			showSuccess("Waste saved successfully.", AppContext.getOwner());
			closeModal(event);

		} catch (IllegalArgumentException e) {
			showError("Validation error", e.getMessage(), AppContext.getOwner());
		} catch (Exception e) {
			e.printStackTrace();
			showError("Unexpected error", e.getMessage(), AppContext.getOwner());
		}
	}

	@FXML
	private void handleAbortWasteCreation(ActionEvent event) {
		closeModal(event);
	}
}