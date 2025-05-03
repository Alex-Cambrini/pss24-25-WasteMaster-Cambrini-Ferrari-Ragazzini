package it.unibo.wastemaster.controller.customer;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.Location;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditCustomerController {

    private Customer customer;    
	private CustomersController customerController;

    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
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

	public void setCustomerController(CustomersController controller) {
		this.customerController = controller;
	}


    public void setCustomerToEdit(Customer customer) {
        this.customer = customer;

        nameField.setText(customer.getName());
        surnameField.setText(customer.getSurname());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        streetField.setText(customer.getLocation().getStreet());
        civicField.setText(customer.getLocation().getCivicNumber());
        cityField.setText(customer.getLocation().getCity());
        postalCodeField.setText(customer.getLocation().getPostalCode());
    }

    @FXML
    private void handleUpdateCustomer() {
        try {
            Customer original = AppContext.customerManager.getCustomerById(customer.getCustomerId());
            if (original == null) {
                DialogUtils.showError("Error", "Customer not found.");
                return;
            }

            boolean changed = !original.getName().equals(nameField.getText()) ||
                    !original.getSurname().equals(surnameField.getText()) ||
                    !original.getPhone().equals(phoneField.getText()) ||
                    !original.getEmail().equals(emailField.getText()) ||
                    !original.getLocation().getStreet().equals(streetField.getText()) ||
                    !original.getLocation().getCivicNumber().equals(civicField.getText()) ||
                    !original.getLocation().getCity().equals(cityField.getText()) ||
                    !original.getLocation().getPostalCode().equals(postalCodeField.getText());

            if (!changed) {
                DialogUtils.showError("No changes", "No fields were modified.");
                return;
            }

            customer.setName(nameField.getText());
            customer.setSurname(surnameField.getText());
            customer.setPhone(phoneField.getText());
            customer.setEmail(emailField.getText());
            customer.setLocation(new Location(
                    streetField.getText(),
                    civicField.getText(),
                    cityField.getText(),
                    postalCodeField.getText()));

            AppContext.customerManager.updateCustomer(customer);
            DialogUtils.showSuccess("Customer updated successfully.");
            customerController.returnToCustomerView();

        } catch (IllegalArgumentException e) {
            DialogUtils.showError("Validation error", e.getMessage());
        }
    }

    @FXML
    private void handleAbortCustomerEdit() {
        customerController.returnToCustomerView();
    }
}
