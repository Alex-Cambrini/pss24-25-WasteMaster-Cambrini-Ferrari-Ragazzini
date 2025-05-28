package it.unibo.wastemaster.controller.customer;

import static it.unibo.wastemaster.controller.utils.DialogUtils.showError;
import static it.unibo.wastemaster.controller.utils.DialogUtils.showSuccess;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.core.utils.ValidateUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller responsible for handling the logic of the "Add Customer" view.
 */

public final class AddCustomerController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField postalCodeField;


    @FXML
    private void handleSaveCustomer(final ActionEvent event) {
        try {
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

            ValidateUtils.validateAll(customer, location);

            if (AppContext.getCustomerDAO().existsByEmail(email)) {
                throw new IllegalArgumentException("- Email is already in use");
            }

            AppContext.getCustomerManager().addCustomer(customer);
            showSuccess("Customer saved successfully.", AppContext.getOwner());
            DialogUtils.closeModal(event);

        } catch (IllegalArgumentException e) {
            showError("Validation error", e.getMessage(), AppContext.getOwner());
        }
    }

    @FXML
    private void handleAbortCustomerCreation(final ActionEvent event) {
        DialogUtils.closeModal(event);
    }
}
