package Client.Controllers.MainPage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.LoginPage.DemoPageController;
import Client.Controllers.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static javafx.scene.paint.Color.*;

public class ProfilePageController {

    User user = SessionManager.getCurrentUser();

    @FXML
    private VBox profilePane, editPane, passwordPane, notificationPane, historyPane;

    @FXML
    private Label tabProfile, tabPassword, tabNotification, tabHistory;

    @FXML
    private Label usernameLabel, aboutLabel, phoneLabel, emailLabel, roleLabel;

    @FXML
    private TextArea aboutField;

    @FXML
    private Label passwordChangeStatus;

    @FXML
    private TextField phoneField, emailField, oldPasswordField, newPasswordField, newPasswordConfirmField;

    //  STYLE
    private final String ACTIVE =
            "-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-cursor: hand;";

    private final String NORMAL =
            "-fx-text-fill: #475569; -fx-cursor: hand;";


    // INIT
    @FXML
    public void initialize() {
        loadUserData();
        showProfile();
    }

    private void loadUserData() {

        usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
        phoneLabel.setText(user.getPhoneNumber());
        emailLabel.setText(user.getEmail());
        if (user.getAbout() == null || user.getAbout().isEmpty()) {
            aboutLabel.setText("Not yet set");
        } else {
            aboutLabel.setText(user.getAbout());
        }
        if (user.isAdmin()) {
            roleLabel.setText("Admin");
        }
        else {
            roleLabel.setText("Member");
        }
    }

    private void loadHistoryData() {
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
        loadUserData();
        hideAll();
        profilePane.setVisible(true);
        setActive(tabProfile);
    }

    @FXML
    private void showEditProfile() {
        hideAll();
        editPane.setVisible(true);

        aboutField.setText(aboutLabel.getText());
        phoneField.setText(phoneLabel.getText());
        emailField.setText(emailLabel.getText());
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
        loadHistoryData();
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
        
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAbout(about);

        showProfile();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }

    public void handleChangePassword(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPasswordConfirm = newPasswordConfirmField.getText();

        passwordChangeStatus.setTextFill(RED);
        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.equals(newPasswordConfirm)) {
                if (newPassword != null) {
                    user.setPassword(newPassword);
                    passwordChangeStatus.setTextFill(WHITE);
                    passwordChangeStatus.setText("Object updated. New password is: " + user.getPassword());
                } else {
                    passwordChangeStatus.setText("Please enter your new password");
                }
            } else {
                passwordChangeStatus.setText("New passwords do not match.");
            }
        } else {
            passwordChangeStatus.setText("Old password check failed.");
        }
    }

}
