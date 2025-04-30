package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddCustomerController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField postalCodeField;

    @FXML
    private void handleSaveCustomer() {
        String email = emailField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        String phone = phoneField.getText();
        String street = streetField.getText();
        String civic = civicField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();

        Location location = new Location(street, civic, city, postalCode);
        Customer customer = new Customer(name, surname, location, email, phone);

        AppContext.customerManager.addCustomer(customer);
        System.out.println("Customer data saved: " + name + " " + surname);        
    }
}
