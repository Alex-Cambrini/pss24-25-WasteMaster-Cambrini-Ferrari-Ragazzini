package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.core.context.AppContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;

import java.util.List;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.viewmodels.CustomerRow;

public class CustomersController {

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
}