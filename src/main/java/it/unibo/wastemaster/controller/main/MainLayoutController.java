package it.unibo.wastemaster.controller.main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MainLayoutController {

    private static MainLayoutController instance;
    private String previousTitle;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label pageTitleLabel;

    @FXML
    public void initialize() {
        instance = this;
        setPageTitle("Welcome back");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            setAlignmentTopCenter(view);

            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T loadCenterWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            setAlignmentTopCenter(view);
            rootPane.setCenter(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleCustomers() {
        setPageTitle("Customer Management");
        MainLayoutController.getInstance().loadCenter("/layouts/customer/CustomersView.fxml");
    }

    @FXML
    private void handleDashboard() {
        setPageTitle("Dashboard");
        rootPane.setCenter(null);
    }

    @FXML
    private void handleVehicle() {
        setPageTitle("Vehicle Management");
        MainLayoutController.getInstance().loadCenter("/layouts/vehicle/VehicleView.fxml");
    }

    public void setPageTitle(String title) {
        if (title != null) {
            previousTitle = pageTitleLabel.getText();
            pageTitleLabel.setText(title);
        }
    }

    public void restorePreviousTitle() {
        if (previousTitle != null) {
            pageTitleLabel.setText(previousTitle);
        }
    }

    private static void setAlignmentTopCenter(javafx.scene.Node node) {
        if (node != null) {
            BorderPane.setAlignment(node, Pos.TOP_CENTER);
        }
    }

}
