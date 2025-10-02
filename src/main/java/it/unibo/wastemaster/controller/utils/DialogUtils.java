package it.unibo.wastemaster.controller.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Utility class for displaying dialog windows and alerts in the application.
 * Provides methods for showing error/success dialogs, confirmation dialogs,
 * modal windows with controllers, and closing modals.
 */
public final class DialogUtils {

    private DialogUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Shows an error alert dialog.
     *
     * @param title the title of the dialog
     * @param message the message content
     * @param owner the parent stage
     */
    public static void showError(
            final String title,
            final String message,
            final Stage owner
    ) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText("Please fix the following errors:");
        alert.setContentText(message);
        Scene scene = alert.getDialogPane().getScene();
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        alert.showAndWait();
    }

    /**
     * Shows a success alert dialog.
     *
     * @param message the message content
     * @param owner the parent stage
     */
    public static void showSuccess(final String message, final Stage owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Scene scene = alert.getDialogPane().getScene();
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        alert.showAndWait();
    }

    /**
     * Shows a modal window and returns its controller.
     *
     * @param <T> the controller type
     * @param title the dialog title
     * @param fxmlPath the path to the FXML file
     * @param owner the parent stage
     * @param controllerInitializer logic to initialize the controller
     * @return the controller wrapped in an Optional
     * @throws IOException if loading the FXML fails
     */
    public static <T> Optional<T> showModalWithController(
            final String title,
            final String fxmlPath,
            final Stage owner,
            final Consumer<T> controllerInitializer) throws IOException {

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
            double centerX = owner.getX()
                + (owner.getWidth() - dialogStage.getWidth()) / 2;
            double centerY = owner.getY()
                + (owner.getHeight() - dialogStage.getHeight()) / 2;
            dialogStage.setX(centerX);
            dialogStage.setY(centerY);
        });
        dialogStage.showAndWait();

        return Optional.ofNullable(controller);
    }

    /**
     * Creates a Scene applying the current application's styles.
     *
     * @param root the root node
     * @param owner the owner stage for inheriting styles
     * @return the scene
     */
    public static Scene createSceneWithCss(final Parent root, final Stage owner) {
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());
        return scene;
    }

    /**
     * Closes the modal window that triggered the given action.
     *
     * @param event the action event
     */
    public static void closeModal(final ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    /**
     * Shows a generic confirmation alert dialog.
     *
     * @param title the dialog title
     * @param message the message content
     * @param owner the parent stage
     * @return true if the user confirmed, false otherwise
     */
    public static boolean showConfirmationDialog(
            final String title,
            final String message,
            final Stage owner) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Scene scene = alert.getDialogPane().getScene();
        scene.getStylesheets().addAll(owner.getScene().getStylesheets());

        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }
}
