package Client.Controllers.LoginPage;

import Branch.AuthService;
import Branch.Member;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

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

    public void setMainController(DemoPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void goToLogin() throws IOException {
        mainController.showLogin();
    }

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        String password = passwordField.getText();
        Member member = AuthService.registerNewUser(firstName, lastName, email, phoneNumber, password);

        try {
            if (member != null) {
                goToLogin();
            }
            else {
                System.out.println("Registration failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}