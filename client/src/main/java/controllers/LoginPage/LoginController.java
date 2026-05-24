package controllers.LoginPage;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

import controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import models.Exceptions.CustomisedException;
import com.group7.dto.auth.*;
import models.SessionManager;
import services.AuthApiService;
import services.AuthSessionMapper;

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
    private final AuthApiService authApiService = new AuthApiService();

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
                AuthResponse response = authApiService.login(new LoginRequest(emailInput, passwordInput));
                SessionManager.loginCurrentUser(AuthSessionMapper.toMember(response));
                loginLabel.setTextFill(GREEN);
                loginLabel.setText("Login successful!");
                mainController.onSuccessfulLogin();
            } catch (IllegalArgumentException e) {
                String message =  e.getMessage();
                loginLabel.setTextFill(RED);
                loginLabel.setText(message);
            } catch (CustomisedException e) {
                String message =  e.getMessage();
                loginLabel.setTextFill(RED);
                loginLabel.setText(message);
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void rememberUser() {
        SceneManager.setRememberUser(rememberMeCheckbox.isSelected());
        System.out.println("Checkbox ticked");
    }
}
