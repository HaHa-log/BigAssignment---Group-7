package Client.Controllers.MainPage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.LoginPage.DemoPageController;
import Client.Controllers.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ProfilePageController {

    @FXML
    private VBox profilePane, editPane, passwordPane, notificationPane, historyPane;
    
    @FXML
    private Label tabProfile, tabPassword, tabNotification, tabHistory;

    @FXML
    private Label aboutLabel, phoneLabel, emailLabel;

    @FXML
    private TextArea aboutField;
    @FXML
    private TextField phoneField, emailField;

    //  STYLE
    private final String ACTIVE =
            "-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-cursor: hand;";

    private final String NORMAL =
            "-fx-text-fill: white; -fx-cursor: hand;";


    // INIT
    @FXML
    public void initialize() {
        showProfile();
    }

    //  TAB COLOR
    private void setActive(Label tab) {
        tabProfile.setStyle(NORMAL);
        tabPassword.setStyle(NORMAL);
        tabNotification.setStyle(NORMAL);
        tabHistory.setStyle(NORMAL);

        tab.setStyle(ACTIVE);
    }

    private void hideAll() {
        profilePane.setVisible(false);
        editPane.setVisible(false);
        passwordPane.setVisible(false);
        notificationPane.setVisible(false);
        historyPane.setVisible(false);
    }

    @FXML
    private void showProfile() {
        hideAll();
        profilePane.setVisible(true);
        setActive(tabProfile);
    }

    @FXML
    private void showEditProfile() {
        hideAll();
        editPane.setVisible(true);

        aboutField.setText(aboutLabel.getText());
        phoneField.setText(phoneLabel.getText().replace("Phone: ", ""));
        emailField.setText(emailLabel.getText().replace("Email: ", ""));
    }

    @FXML
    private void cancelEdit() {
        showProfile();
    }

    @FXML
    private void showChangePassword() {
        hideAll();
        passwordPane.setVisible(true);
        setActive(tabPassword);
    }

    @FXML
    private void showNotification() {
        hideAll();
        notificationPane.setVisible(true);
        setActive(tabNotification);
    }

    @FXML
    private void showHistory() {
        hideAll();
        historyPane.setVisible(true);
        setActive(tabHistory);
    }

    // SAVE PROFILE
    @FXML
    public void handleSave(ActionEvent event) {

        String about = aboutField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        aboutLabel.setText(about);
        phoneLabel.setText("Phone: " + phone);
        emailLabel.setText("Email: " + email);


        showProfile();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }

