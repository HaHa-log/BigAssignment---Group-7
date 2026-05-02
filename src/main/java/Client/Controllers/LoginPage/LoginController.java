package Client.Controllers.LoginPage;

import static Branch.AuthService.*;
import static javafx.scene.paint.Color.RED;

import Branch.SessionManager;
import Branch.User;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginLabel;
    @FXML
    private CheckBox rememberMeCheckbox;

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

        if (passwordInput.isEmpty() || emailInput.isEmpty()) {
            loginLabel.setTextFill(RED);
            loginLabel.setText("PLease enter your passwword and email!");
        }
        else {
            try {
                User loginResult = login(emailInput, passwordInput);
                mainController.onSuccessfulLogin();
            } catch (IllegalArgumentException e) {
                String message =  e.getMessage();
                loginLabel.setTextFill(RED);
                loginLabel.setText(message);
            }
        }
    }

    @FXML
    private void rememberUser() {
        SceneManager.setRememberUser(rememberMeCheckbox.isSelected());
        System.out.println("Checkbox ticked");
    }
}