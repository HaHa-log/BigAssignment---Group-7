package Client.Controllers.MainPage.ProfilePage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController extends BaseController {

    @FXML
    private Label phoneLabel, emailLabel, aboutLabel;

    @Override
    protected void initData() {
        phoneLabel.setText(user.getPhoneNumber());
        emailLabel.setText(user.getEmail());
        aboutLabel.setText(EditProfileController.getAboutText());
    }
}
