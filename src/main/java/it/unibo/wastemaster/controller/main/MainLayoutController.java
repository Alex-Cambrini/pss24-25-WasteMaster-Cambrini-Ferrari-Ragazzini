package it.unibo.wastemaster.controller.main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    private static MainLayoutController instance;
    private String previousTitle;

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane centerPane;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Hyperlink dashboardLink;
    @FXML
    private Hyperlink clientiLink;
    @FXML
    private Hyperlink ScheduleLink;
    @FXML
    private Hyperlink veicoliLink;
    @FXML
    private Hyperlink employeeLink;

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
            centerPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T loadCenterWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            centerPane.getChildren().setAll(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleCustomers() {
        clientiLink.setVisited(false);
        setPageTitle("Customer Management");
        MainLayoutController.getInstance().loadCenter("/layouts/customer/CustomersView.fxml");
    }

    @FXML
    private void handleDashboard() {
        dashboardLink.setVisited(false);
        setPageTitle("Dashboard");
        rootPane.getChildren().clear();
    }

    @FXML
    private void handleVehicle() {
        veicoliLink.setVisited(false);
        setPageTitle("Vehicle Management");
        MainLayoutController.getInstance().loadCenter("/layouts/vehicle/VehicleView.fxml");
    }

    @FXML
    private void handleEmployee() {
        employeeLink.setVisited(false);
        setPageTitle("Employee Management");
        MainLayoutController.getInstance().loadCenter("/layouts/employee/EmployeeView.fxml");
    }

    @FXML
    private void handleSchedule() {
        ScheduleLink.setVisited(false);
        setPageTitle("Schedule Management");
        MainLayoutController.getInstance().loadCenter("/layouts/schedule/ScheduleView.fxml");
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

    public StackPane getRootPane() {
        return rootPane;
    }
}
