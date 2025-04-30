package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class CustomersController {

    @FXML
    private Button addClientButton;

    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleAddClient() {
        MainLayoutController
                .getInstance()
                .loadCenter("/layouts/customer/AddCustomerView.fxml");
    }
}
