package it.unibo.wastemaster.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import it.unibo.wastemaster.core.context.AppContext;

public class JavaFXApp extends Application {

	@Override
	public void start(Stage primaryStage) {

		try {
			AppContext.init();
			AppContext.setOwner(primaryStage);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/main/MainLayout.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/primer-light.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("WasteMaster");
			primaryStage.setWidth(1000);
			primaryStage.setHeight(700);
			primaryStage.setMinWidth(1000);
			primaryStage.setMinHeight(700);
			primaryStage.show();

		} catch (Exception e) {
			System.err.println("Critical Error: " + e.getMessage());
			e.printStackTrace();
			Platform.exit();
		}
	}
}