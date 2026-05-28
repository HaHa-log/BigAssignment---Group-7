package controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import models.SessionManager;
import services.UserApiService;

import static javafx.scene.paint.Color.RED;

public class PasswordController extends BaseController {

    @FXML private PasswordField oldPasswordField, newPasswordField, newPasswordConfirmField;
    @FXML private Label passwordChangeStatus;
    private final UserApiService userApiService = new UserApiService();

    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirm = newPasswordConfirmField.getText();

        passwordChangeStatus.setTextFill(RED);

        if (!newPassword.equals(confirm) || newPassword.isEmpty()) {
            passwordChangeStatus.getStyleClass().setAll("error");
            passwordChangeStatus.setText("Check match or empty fields.");
            return;
        }

        try {
            user = userApiService.changePassword(user.getId(), oldPassword, newPassword);
            SessionManager.updateCurrentUser(user);
            passwordChangeStatus.getStyleClass().setAll("success");
            passwordChangeStatus.setText("Password updated!");
            oldPasswordField.clear();
            newPasswordField.clear();
            newPasswordConfirmField.clear();
        } catch (Exception e) {
            passwordChangeStatus.getStyleClass().setAll("error");
            passwordChangeStatus.setText(e.getMessage());
        }
    }
}
