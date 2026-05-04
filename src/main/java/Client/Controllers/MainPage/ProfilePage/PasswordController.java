package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import static javafx.scene.paint.Color.*;

public class PasswordController extends BaseController {

    @FXML private PasswordField oldPasswordField, newPasswordField, newPasswordConfirmField;
    @FXML private Label passwordChangeStatus;

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirm = newPasswordConfirmField.getText();

        String successStyle = "-fx-background-color: white; " + "-fx-text-fill: green; " + "-fx-font-weight: bold; " + "-fx-border-color: green; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; " + "-fx-padding: 5;";
        String errorStyle = "-fx-background-color: white; " + "-fx-text-fill: red; " + "-fx-font-weight: bold; " + "-fx-border-color: red; " + "-fx-border-radius: 5; " +"-fx-background-radius: 5; " + "-fx-padding: 5;";

        passwordChangeStatus.setTextFill(RED);

        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.equals(confirm) && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
                passwordChangeStatus.setStyle(successStyle);
                passwordChangeStatus.setText("Password updated!");
            } else {
                passwordChangeStatus.setStyle(errorStyle);
                passwordChangeStatus.setText("Check match or empty fields.");
            }
        } else {
            passwordChangeStatus.setStyle(errorStyle);
            passwordChangeStatus.setText("Old password failed.");
        }
    }
}
