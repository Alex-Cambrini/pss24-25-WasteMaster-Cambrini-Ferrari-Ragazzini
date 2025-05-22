package it.unibo.wastemaster.controller.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

	public static <T> Optional<T> showModalWithController(
			String title,
			String fxmlPath,
			Stage owner,
			Consumer<T> controllerInitializer) throws IOException {

		FXMLLoader loader = new FXMLLoader(DialogUtils.class.getResource(fxmlPath));
		Parent root = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.setTitle(title);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.initOwner(owner);
		Scene scene = createSceneWithCss(root, owner);
		dialogStage.setScene(scene);

		T controller = loader.getController();
		controllerInitializer.accept(controller);

		dialogStage.setOnShown(e -> {
			dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
			dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
		});
		dialogStage.showAndWait();

		return Optional.ofNullable(controller);
	}

	public static Scene createSceneWithCss(Parent root, Stage owner) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(owner.getScene().getStylesheets());
		return scene;
	}

}
