package Client.Controllers.LoginPage;

import static Branch.AuthService.*;
import Branch.User;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    private DemoPageController mainController;

    public void setMainController(DemoPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void goToRegister() throws IOException {
        mainController.showRegister();
    }

    @FXML
    private void handleLogin () {
        String emailInput = emailField.getText();
        String passwordInput = passwordField.getText();

        User loginResult = login(emailInput, passwordInput);
        if (loginResult != null) {
            mainController.onSuccessfulLogin();
        }
        else {
            System.out.println("Login failed!");
        }
    }
}