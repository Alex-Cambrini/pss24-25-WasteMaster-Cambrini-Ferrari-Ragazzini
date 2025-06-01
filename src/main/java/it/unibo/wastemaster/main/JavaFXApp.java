package it.unibo.wastemaster.main;

import it.unibo.wastemaster.core.context.AppContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    private Parent root;

    @Override
    public void start(Stage primaryStage) {
        try {
            AppContext.init();
            AppContext.setOwner(primaryStage);

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource("/layouts/login/LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/primer-light.css")
                    .toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("WasteMaster - Login");
            primaryStage.getIcons()
                    .add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            primaryStage.setWidth(400);
            primaryStage.setHeight(300);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }
}
