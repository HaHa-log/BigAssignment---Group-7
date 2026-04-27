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
    private TextField phoneField, emailField, oldPasswordField, newPasswordField, newPasswordConfirmField;

    //  STYLE
    private final String ACTIVE =
            "-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-cursor: hand;";

    private final String NORMAL =
            "-fx-text-fill: white; -fx-cursor: hand;";


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
        aboutLabel.setText("Not yet set");
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

        showProfile();
    }

    public void handleChangePassword(ActionEvent event) {
        System.out.println("Current User ID: " + user.getId()); // Verify ID exists
        String newPassword = newPasswordField.getText();
        if (user.getPassword().equals(oldPasswordField.getText())) {
            if (newPasswordField.getText().equals(newPasswordConfirmField.getText())) {
                try {
                    user.setPassword(newPassword);
                    System.out.println("Object updated. New password is: " + user.getPassword());
                } catch (Exception e) {
                    System.out.println("Validation failed: " + e.getMessage());
                }
            } else {
                System.out.println("New passwords do not match.");
            }
        } else {
            System.out.println("Old password check failed.");
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }
}
