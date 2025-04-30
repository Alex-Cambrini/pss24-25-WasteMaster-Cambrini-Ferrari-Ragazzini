package it.unibo.wastemaster.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import it.unibo.wastemaster.core.context.AppContext;
public class JavaFXApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		AppContext.init();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/main/MainLayout.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("WasteMaster");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
