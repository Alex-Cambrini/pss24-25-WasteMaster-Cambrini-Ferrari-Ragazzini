package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.core.context.AppContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.util.List;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.viewmodels.CustomerRow;

public class CustomersController {

    private Timeline refreshTimeline;

    @FXML
    private Button addClientButton;

    @FXML
    private TableView<CustomerRow> customerTable;
    @FXML
    private TableColumn<CustomerRow, String> nameColumn;
    @FXML
    private TableColumn<CustomerRow, String> surnameColumn;
    @FXML
    private TableColumn<CustomerRow, String> emailColumn;
    @FXML
    private TableColumn<CustomerRow, String> streetColumn;
    @FXML
    private TableColumn<CustomerRow, String> civicColumn;
    @FXML
    private TableColumn<CustomerRow, String> cityColumn;
    @FXML
    private TableColumn<CustomerRow, String> postalColumn;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        civicColumn.setCellValueFactory(new PropertyValueFactory<>("civic"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        loadCustomers();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> loadCustomers()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopAutoRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void loadCustomers() {
        List<Object[]> rawData = AppContext.customerDAO.findCustomerDetails();
        ObservableList<CustomerRow> rows = FXCollections.observableArrayList();

        for (Object[] row : rawData) {
            rows.add(new CustomerRow(
                    (String) row[0], // name
                    (String) row[1], // surname
                    (String) row[2], // email
                    (String) row[3], // street
                    (String) row[4], // civicNumber
                    (String) row[5], // city
                    (String) row[6] // postalCode
            ));
        }

        customerTable.setItems(rows);
    }

    @FXML
    private void handleAddClient() {
        MainLayoutController
                .getInstance()
                .loadCenter("/layouts/customer/AddCustomerView.fxml");
    }

    @FXML
    private void handleDelete() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a customer to delete.");
            return;
        }

        var customer = AppContext.customerDAO.findByEmail(selected.getEmail());

        if (customer == null) {
            DialogUtils.showError("Not Found", "The selected customer could not be found.");
            return;
        }

        boolean success = AppContext.customerManager.softDeleteCustomer(customer);
        if (success) {
            DialogUtils.showSuccess("Customer deleted successfully.");
            loadCustomers();
        } else {
            DialogUtils.showError("Deletion Failed", "Unable to delete the selected customer.");
        }
    }

    @FXML
    private void handleEditCustomer() {
        CustomerRow selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtils.showError("No Selection", "Please select a customer to edit.");
            return;
        }

        var customer = AppContext.customerDAO.findByEmail(selected.getEmail());
        if (customer == null) {
            DialogUtils.showError("Not Found", "Customer not found.");
            return;
        }

        try {
            var loader = new FXMLLoader(getClass().getResource("/layouts/customer/EditCustomerView.fxml"));
            Parent root = loader.load();
            EditCustomerController controller = loader.getController();
            controller.setCustomerToEdit(customer);
            MainLayoutController.getInstance().setCenter(root);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Edit view.");
        }
    }

}