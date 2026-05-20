package controllers.LoginPage;

import models.dto.auth.RegisterRequest;
import models.services.AuthApiService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class RegisterController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private PasswordField passwordField;
    private DemoPageController mainController;

    @FXML
    private Label registerLabel;

    public void setMainController(DemoPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void goToLogin() throws IOException {
        mainController.showLogin();
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText();
        String password = passwordField.getText();

        registerLabel.setTextFill(RED);

        if (firstName == null || lastName == null || email == null || phoneNumber == null || password == null) {
            registerLabel.setText("Please fill in all the fields");
            return ;
        }

        try {
            AuthApiService authService = new AuthApiService();
            RegisterRequest req = new RegisterRequest(firstName, lastName, email, phoneNumber, password, null);

            authService.register(req);

            registerLabel.setTextFill(GREEN);
            registerLabel.setText("User registered successfully!");

            mainController.showLogin();
        } catch (IllegalArgumentException e) {
            registerLabel.setTextFill(RED);
            registerLabel.setText(e.getMessage());
        } catch (IOException | InterruptedException e) {
            registerLabel.setTextFill(RED);
            registerLabel.setText("Cannot connect to server.");
        }
    }
}