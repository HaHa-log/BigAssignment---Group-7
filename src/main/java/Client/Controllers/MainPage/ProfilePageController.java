package Client.Controllers.MainPage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ProfilePageController {

    // PANES
    @FXML private VBox profilePane;
    @FXML private VBox historyPane;
    @FXML private VBox passwordPane;
    @FXML private VBox settingPane;

    // PROFILE INFO
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label bioLabel;
    @FXML private ImageView avatar;

    // HISTORY
    @FXML private TableView<String> historyTable;

    // SETTING
    @FXML private TextField usernameField;
    @FXML private TextField emailField;

    // PASSWORD
    @FXML private PasswordField oldPass;
    @FXML private PasswordField newPass;
    @FXML private PasswordField confirmPass;

    //  SWITCH PANE
    private void hideAll() {
        profilePane.setVisible(false);
        historyPane.setVisible(false);
        passwordPane.setVisible(false);
        settingPane.setVisible(false);
    }

    @FXML
    public void handleProfile() {
        hideAll();
        profilePane.setVisible(true);
    }

    @FXML
    public void handleHistory() {
        hideAll();
        historyPane.setVisible(true);
    }

    @FXML
    public void handlePassword() {
        hideAll();
        passwordPane.setVisible(true);
    }

    @FXML
    public void handleSetting() {
        hideAll();
        settingPane.setVisible(true);

        usernameField.setText(nameLabel.getText());
        emailField.setText(emailLabel.getText());
    }

    //INIT
    @FXML
    public void initialize() {
        loadUserData();
        loadHistoryData();
    }

    //  LOAD USER
    private void loadUserData() {
    }

    // LOAD HISTORY
    private void loadHistoryData() {
    }

    @FXML
    private void handleSaveProfile() {
        nameLabel.setText(usernameField.getText());
        emailLabel.setText(emailField.getText());

        handleProfile();
    }
}
