package it.unibo.wastemaster.controller.utils;

import javafx.scene.control.Alert;

public class DialogUtils {

	public static void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText("Please fix the following errors:");
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}