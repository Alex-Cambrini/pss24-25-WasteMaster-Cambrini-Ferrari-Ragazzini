package it.unibo.wastemaster.controller.main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MainLayoutController {
    private static MainLayoutController instance;
    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        instance = this;
    }

    public static MainLayoutController getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "MainLayoutController not initialized");
        }
        return instance;
    }

    public void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));
            Pane view = loader.load();
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCustomers() {
        MainLayoutController.getInstance().loadCenter("/layouts/customer/CustomersView.fxml");
    }

    public void setCenter(javafx.scene.Node node) {
        rootPane.setCenter(node);
    }
    
}
