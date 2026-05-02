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

        passwordChangeStatus.setTextFill(RED);

        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.equals(confirm) && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
                passwordChangeStatus.setTextFill(GREEN);
                passwordChangeStatus.setText("Password updated!");
            } else {
                passwordChangeStatus.setText("Check match or empty fields.");
            }
        } else {
            passwordChangeStatus.setText("Old password failed.");
        }
    }
}
