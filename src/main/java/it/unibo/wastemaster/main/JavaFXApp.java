package it.unibo.wastemaster.main;

import it.unibo.wastemaster.application.context.AppContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX application entry point for WasteMaster.
 * <p>
 * This class initializes the application context and loads the login view.
 */
public final class JavaFXApp extends Application {

    /**
     * Default window width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * Default window height.
     */
    private static final int DEFAULT_HEIGHT = 300;

    private Parent root;

    /**
     * Starts the JavaFX application by initializing the context,
     * loading the login view, and configuring the primary stage.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(final Stage primaryStage) {
        try {
            AppContext.init();
            AppContext.setOwner(primaryStage);

            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/layouts/login/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/primer-light.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("WasteMaster - Login");
            primaryStage.getIcons()
                    .add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            primaryStage.setWidth(DEFAULT_WIDTH);
            primaryStage.setHeight(DEFAULT_HEIGHT);
            primaryStage.setMinWidth(DEFAULT_WIDTH);
            primaryStage.setMinHeight(DEFAULT_HEIGHT);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }
}
