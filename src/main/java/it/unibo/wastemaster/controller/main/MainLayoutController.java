package it.unibo.wastemaster.controller.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Controller for the main layout of the application.
 */
public final class MainLayoutController {

    private static final Logger LOGGER =
            Logger.getLogger(MainLayoutController.class.getName());

    private static final String ERROR_LOADING_FXML = "Error loading FXML";
    private static final String CUSTOMER_MANAGEMENT = "Customer Management";
    private static final String WASTE_MANAGEMENT = "Waste Management";
    private static final String VEHICLE_MANAGEMENT = "Vehicle Management";
    private static final String EMPLOYEE_MANAGEMENT = "Employee Management";
    private static final String SCHEDULE_MANAGEMENT = "Schedule Management";

    private static MainLayoutController instance;
    private Object currentController;
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
    private Hyperlink wasteLink;

    @FXML
    private Hyperlink scheduleLink;

    @FXML
    private Hyperlink veicoliLink;

    @FXML
    private Hyperlink employeeLink;

    /**
     * Initializes the controller after its root element has been completely processed.
     */
    @FXML
    public void initialize() {
        MainLayoutController.setInstance(this);
        setPageTitle("Welcome back");
    }

    private static void setInstance(final MainLayoutController controller) {
        MainLayoutController.instance = controller;
    }

    /**
     * Returns the singleton instance of this controller.
     *
     * @return the controller instance
     * @throws IllegalStateException if the controller has not been initialized
     */
    public static MainLayoutController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MainLayoutController not initialized");
        }
        return instance;
    }

    /**
     * Loads the specified FXML file into the center pane.
     *
     * @param fxmlPath the path to the FXML file
     */
    public void loadCenter(final String fxmlPath) {
        try {
            invokeStopAutoRefresh();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            final Pane view = loader.load();
            currentController = loader.getController();
            centerPane.getChildren().setAll(view);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_LOADING_FXML, e);
        }
    }

    /**
     * Loads the specified FXML file into the center pane and returns its controller.
     *
     * @param <T> the type of the controller
     * @param fxmlPath the path to the FXML file
     * @return the controller instance, or null if loading fails
     */
    public <T> T loadCenterWithController(final String fxmlPath) {
        try {
            invokeStopAutoRefresh();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            final Pane view = loader.load();
            T controller = loader.getController();
            currentController = controller;

            centerPane.getChildren().setAll(view);
            return controller;
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_LOADING_FXML, e);
            return null;
        }
    }

    private void invokeStopAutoRefresh() {
        if (currentController != null) {
            try {
                currentController.getClass().getMethod("stopAutoRefresh")
                        .invoke(currentController);
            } catch (NoSuchMethodException e) {
                // Method stopAutoRefresh does not exist; safe to ignore.
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error invoking stopAutoRefresh", e);
            }
        }
    }

    @FXML
    private void handleCustomers() {
        clientiLink.setVisited(false);
        setPageTitle(CUSTOMER_MANAGEMENT);
        loadCenter("/layouts/customer/CustomersView.fxml");
    }

    @FXML
    private void handleDashboard() {
        // Future implementation
    }

    @FXML
    private void handleWaste() {
        wasteLink.setVisited(false);
        setPageTitle(WASTE_MANAGEMENT);
        loadCenter("/layouts/waste/WasteView.fxml");
    }

    @FXML
    private void handleVehicle() {
        veicoliLink.setVisited(false);
        setPageTitle(VEHICLE_MANAGEMENT);
        loadCenter("/layouts/vehicle/VehicleView.fxml");
    }

    @FXML
    private void handleEmployee() {
        employeeLink.setVisited(false);
        setPageTitle(EMPLOYEE_MANAGEMENT);
        loadCenter("/layouts/employee/EmployeeView.fxml");
    }

    @FXML
    private void handleSchedule() {
        scheduleLink.setVisited(false);
        setPageTitle(SCHEDULE_MANAGEMENT);
        loadCenter("/layouts/schedule/ScheduleView.fxml");
    }

    /**
     * Sets the page title label.
     *
     * @param title the title to set
     */
    public void setPageTitle(final String title) {
        if (title != null) {
            previousTitle = pageTitleLabel.getText();
            pageTitleLabel.setText(title);
        }
    }

    /**
     * Restores the previous page title.
     */
    public void restorePreviousTitle() {
        if (previousTitle != null) {
            pageTitleLabel.setText(previousTitle);
        }
    }

    /**
     * Returns the root pane of the layout.
     *
     * @return the root pane
     */
    public StackPane getRootPane() {
        return rootPane;
    }
}
