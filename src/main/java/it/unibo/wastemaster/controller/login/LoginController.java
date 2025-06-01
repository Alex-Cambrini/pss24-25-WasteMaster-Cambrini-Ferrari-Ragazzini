package it.unibo.wastemaster.controller.login;

import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.dao.AccountDAO;
import it.unibo.wastemaster.core.services.LoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for handling the login UI logic.
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final LoginService loginService =
            new LoginService(new AccountDAO(AppContext.getEntityManager()));

    /**
     * Handles the login button action.
     * Authenticates the user and loads the main UI on success.
     * Shows error message on failure or exception.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (loginService.authenticate(email, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/layouts/main/MainLayout.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/primer-light.css")
                        .toExternalForm());

                Stage stage = AppContext.getOwner();
                stage.setScene(scene);
                stage.setTitle("WasteMaster");
                stage.setWidth(1000);
                stage.setHeight(700);
                stage.setMinWidth(1000);
                stage.setMinHeight(700);

            } catch (Exception e) {
                errorLabel.setText("Internal error occurred.");
                errorLabel.setVisible(true);
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid email or password.");
            errorLabel.setVisible(true);
        }
    }
}
