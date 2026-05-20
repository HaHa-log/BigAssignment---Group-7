package controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import static javafx.scene.paint.Color.RED;

public class PasswordController extends BaseController {

    @FXML private PasswordField oldPasswordField, newPasswordField, newPasswordConfirmField;
    @FXML private Label passwordChangeStatus;

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirm = newPasswordConfirmField.getText();

        passwordChangeStatus.setTextFill(RED);

        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.equals(confirm) && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
                passwordChangeStatus.getStyleClass().setAll("success");
                passwordChangeStatus.setText("Password updated!");
            } else {
                passwordChangeStatus.getStyleClass().setAll("error");
                passwordChangeStatus.setText("Check match or empty fields.");
            }
        } else {
            passwordChangeStatus.getStyleClass().setAll("error");
            passwordChangeStatus.setText("Old password failed.");
        }
    }
}
