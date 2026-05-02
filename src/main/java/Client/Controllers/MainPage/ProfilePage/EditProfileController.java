package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditProfileController extends BaseController {

    @FXML private TextArea aboutField;
    @FXML private TextField phoneField, emailField;

    @Override
    protected void initData() {
        aboutField.setText("Not yet set");
        phoneField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleSave() {
        user.setPhoneNumber(phoneField.getText());
        user.setEmail(emailField.getText());
    }

    @FXML
    private void cancelEdit() {
        navigation.showProfile();
    }


}
