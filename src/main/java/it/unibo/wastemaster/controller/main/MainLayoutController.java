package it.unibo.wastemaster.controller.main;

import it.unibo.wastemaster.application.context.AppContext;
import it.unibo.wastemaster.controller.customer.CustomersController;
import it.unibo.wastemaster.controller.employee.EmployeeController;
import it.unibo.wastemaster.controller.invoice.InvoiceController;
import it.unibo.wastemaster.controller.schedule.ScheduleController;
import it.unibo.wastemaster.controller.trip.TripController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.controller.vehicle.VehicleController;
import it.unibo.wastemaster.controller.waste.WasteController;
import it.unibo.wastemaster.domain.model.Employee;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller for the main layout of the application.
 */
public final class MainLayoutController {

    private static final int LOGIN_WINDOW_WIDTH = 400;
    private static final int LOGIN_WINDOW_HEIGHT = 300;
    private static final Logger LOGGER =
            Logger.getLogger(MainLayoutController.class.getName());

    private static final String ERROR_LOADING_FXML = "Error loading FXML";
    private static final String CUSTOMER_MANAGEMENT = "Customer Management";
    private static final String WASTE_MANAGEMENT = "Waste Management";
    private static final String VEHICLE_MANAGEMENT = "Vehicle Management";
    private static final String EMPLOYEE_MANAGEMENT = "Employee Management";
    private static final String SCHEDULE_MANAGEMENT = "Schedule Management";
    private static final String TRIP_MANAGEMENT = "Trip Management";
    private static final String INVOICE_MANAGEMENT = "Invoice Management";

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
    private Hyperlink customersLink;

    @FXML
    private Hyperlink wasteLink;

    @FXML
    private Hyperlink schedulesLink;

    @FXML
    private Hyperlink vehiclesLink;

    @FXML
    private Hyperlink employeesLink;

    @FXML
    private Hyperlink tripsLink;

    @FXML
    private Hyperlink invoicesLink;

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

    private static void setInstance(final MainLayoutController controller) {
        MainLayoutController.instance = controller;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     */
    @FXML
    public void initialize() {
        MainLayoutController.setInstance(this);
        String accountName = AppContext.getCurrentAccount().getEmployee().getName();
        setPageTitle(String.format("Welcome back %s", accountName));
        Employee.Role role = AppContext.getCurrentAccount().getEmployee().getRole();
        switch (role) {
            case OFFICE_WORKER -> {
                employeesLink.setDisable(true);
                vehiclesLink.setDisable(true);
            }
            case OPERATOR -> {
                customersLink.setDisable(true);
                employeesLink.setDisable(true);
                vehiclesLink.setDisable(true);
                schedulesLink.setDisable(true);
            }
            default -> {
                // Do nothing
            }
        }
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
        System.out.println("[DEBUG] Loading FXML from path: " + fxmlPath);
        try {
            invokeStopAutoRefresh();
            final URL resource = getClass().getResource(fxmlPath);
            System.out.println("[DEBUG] Resolved resource URL: " + resource);
            if (resource == null) {
                System.out.println("[ERROR] Resource not found: " + fxmlPath);
                return null;
            }
            final FXMLLoader loader = new FXMLLoader(resource);
            final Pane view = loader.load();
            T controller = loader.getController();
            System.out.println("[DEBUG] Controller loaded: " + (controller != null));
            currentController = controller;

            centerPane.getChildren().setAll(view);
            return controller;
        } catch (final IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR] IOException loading FXML: " + e.getMessage());
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

        customersLink.setVisited(false);
        setPageTitle(CUSTOMER_MANAGEMENT);

        System.out.println("[DEBUG] Carico CustomersView.fxml...");
        CustomersController controller =
                loadCenterWithController("/layouts/customer/CustomersView.fxml");

        System.out.println("[DEBUG] Controller caricato: " + (controller != null));

        if (controller != null) {
            var cm = AppContext.getServiceFactory().getCustomerManager();
            System.out.println("[DEBUG] CustomerManager: " + cm);
            controller.setCustomerManager(cm);
            controller.initData();
        }
    }

    @FXML
    private void handleDashboard() {
        // Future implementation
    }

    @FXML
    private void handleWaste() {
        wasteLink.setVisited(false);
        setPageTitle(WASTE_MANAGEMENT);
        WasteController controller = loadCenterWithController("/layouts/waste"
                + "/WasteView.fxml");
        if (controller != null) {
            controller.setWasteManager(AppContext.getServiceFactory()
                    .getWasteManager());
            controller.setWasteScheduleManager(AppContext.getServiceFactory()
                    .getWasteScheduleManager());
        }
        controller.initData();
    }

    @FXML
    private void handleVehicle() {
        vehiclesLink.setVisited(false);
        setPageTitle(VEHICLE_MANAGEMENT);
        VehicleController controller =
                loadCenterWithController("/layouts/vehicle/VehicleView.fxml");
        if (controller != null) {
            controller.setVehicleManager(AppContext.getServiceFactory()
                    .getVehicleManager());
            controller.initData();
        }
    }

    @FXML
    private void handleEmployee() {
        employeesLink.setVisited(false);
        setPageTitle(EMPLOYEE_MANAGEMENT);
        EmployeeController controller =
                loadCenterWithController("/layouts/employee/EmployeeView.fxml");
        if (controller != null) {
            controller.setEmployeeManager(
                    AppContext.getServiceFactory().getEmployeeManager());
            controller.initData();
        }
    }

    @FXML
    private void handleSchedule() {
        schedulesLink.setVisited(false);
        setPageTitle(SCHEDULE_MANAGEMENT);
        ScheduleController controller =
                loadCenterWithController("/layouts/schedule/ScheduleView.fxml");
        if (controller != null) {
            controller.setScheduleManager(
                    AppContext.getServiceFactory().getScheduleManager());
            controller.setRecurringScheduleManager(AppContext.getServiceFactory()
                    .getRecurringScheduleManager());
            controller.setOneTimeScheduleManager(AppContext.getServiceFactory()
                    .getOneTimeScheduleManager());
            controller.setCollectionManager(AppContext.getServiceFactory()
                    .getCollectionManager());
        }
        controller.initData();
    }

    @FXML
    private void handleTrip() {
        tripsLink.setVisited(false);
        setPageTitle(TRIP_MANAGEMENT);

        TripController controller =
                loadCenterWithController("/layouts/trip/TripView.fxml");
        if (controller != null) {
            controller.setTripManager(
                    AppContext.getServiceFactory().getTripManager());
            controller.setVehicleManager(AppContext.getServiceFactory()
                    .getVehicleManager());
            controller.initData();
        }
    }

    @FXML
    private void handleInvoice() {
        invoicesLink.setVisited(false);
        setPageTitle(INVOICE_MANAGEMENT);
        InvoiceController controller =
                loadCenterWithController("/layouts/invoice/InvoiceView.fxml");
        if (controller != null) {
            controller.setInvoiceManager(AppContext.getServiceFactory().getInvoiceManager());
            controller.setCustomerManager(AppContext.getServiceFactory()
                    .getCustomerManager());
            controller.setCollectionManager(AppContext.getServiceFactory()
                    .getCollectionManager());
            controller.initData();
        }
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

    @FXML
    private void handleLogout() {
        if (DialogUtils.showConfirmationDialog(
                "Logout Confirmation",
                "Are you sure you want to logout?",
                AppContext.getOwner())) {
            try {
                AppContext.setCurrentAccount(null);

                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/layouts/login/LoginView.fxml"));
                Parent loginRoot = loader.load();

                Scene loginScene = new Scene(loginRoot);
                loginScene.getStylesheets().add(
                        Objects.requireNonNull(getClass()
                                        .getResource("/css/primer-light.css"))
                                .toExternalForm()
                );

                Stage stage = AppContext.getOwner();
                stage.setScene(loginScene);
                stage.setTitle("WasteMaster - Login");
                stage.setWidth(LOGIN_WINDOW_WIDTH);
                stage.setHeight(LOGIN_WINDOW_HEIGHT);
                stage.setMinWidth(LOGIN_WINDOW_WIDTH);
                stage.setMinHeight(LOGIN_WINDOW_HEIGHT);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error during logout", e);
            }
        }
    }
}
