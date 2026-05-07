package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditProfileController extends BaseController {

    @FXML private TextArea aboutField;
    @FXML private TextField phoneField, emailField;

    private static String aboutText = "Not yet set";

    public static String getAboutText() {
        return aboutText;
    }

    @Override
    protected void initData() {
        aboutField.setText(aboutText);
        phoneField.setText(user.getPhoneNumber());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleSave() {
        user.setPhoneNumber(phoneField.getText());
        user.setEmail(emailField.getText());
        aboutText = aboutField.getText();

    }

    @FXML
    private void cancelEdit() {
        navigation.showProfile();
    }


}
