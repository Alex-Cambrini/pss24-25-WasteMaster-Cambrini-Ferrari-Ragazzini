package it.unibo.wastemaster.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import atlantafx.base.theme.PrimerLight;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;

public class JavaFXApp extends Application {

	@Override
	public void start(Stage primaryStage) {
	try {
		AppContext.init();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/main/MainLayout.fxml"));
		Parent root = loader.load();
		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("WasteMaster");
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(200);
		primaryStage.show();

	} catch (Exception e) {
		DialogUtils.showError("Critical Error:\n", e.getMessage());
		Platform.exit();
	}
}}