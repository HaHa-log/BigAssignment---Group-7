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

    // PANES
    @FXML
    private VBox profilePane, editPane, passwordPane, financePane, notificationPane, historyPane;

    // TABS
    @FXML
    private Label tabProfile, tabPassword, tabFinance, tabNotification, tabHistory;

    // PROFILE INFO
    @FXML
    private Label usernameLabel, aboutLabel, phoneLabel, emailLabel, roleLabel;

    // INPUT FIELDS
    @FXML
    private TextArea aboutField;
    @FXML
    private TextField phoneField, emailField;
    @FXML
    private TextField oldPasswordField, newPasswordField, newPasswordConfirmField;

    // FINANCE
    @FXML
    private VBox depositBox, withdrawBox;
    @FXML
    private TextField depositField, withdrawField, amountField;
    @FXML
    private Label balanceLabel, financeStatus;

    // STATUS
    @FXML
    private Label passwordChangeStatus;

    //  STYLE
    private final String ACTIVE =
            "-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-cursor: hand;";

    private final String NORMAL =
            "-fx-text-fill: #475569; -fx-cursor: hand;";


    // INIT
    @FXML
    public void initialize() {
        loadUserData();
        hideAll();
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
        tabFinance.setStyle(NORMAL);
        tabNotification.setStyle(NORMAL);
        tabHistory.setStyle(NORMAL);

        tab.setStyle(ACTIVE);
    }

    private void hideAll() {
        profilePane.setVisible(false);
        editPane.setVisible(false);
        passwordPane.setVisible(false);
        financePane.setVisible(false);
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
    private void showFinance() {
        hideAll();
        financePane.setVisible(true);
        setActive(tabFinance);
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

    @FXML
    private void handleDeposit() {
        depositBox.setVisible(true);
        depositBox.setManaged(true);

        withdrawBox.setVisible(false);
        withdrawBox.setManaged(false);
    }

    @FXML
    private void handleWithdraw() {
        withdrawBox.setVisible(true);
        withdrawBox.setManaged(true);

        depositBox.setVisible(false);
        depositBox.setManaged(false);
    }

        @FXML
    private void handleSaveDeposit() {
        try {
            double amount = Double.parseDouble(depositField.getText());

            if (amount <= 0) return;

            balance += amount;
            balanceLabel.setText(String.valueOf(balance));

            depositField.clear();
            depositBox.setVisible(false);
            depositBox.setManaged(false);

        } catch (Exception e) {
            System.out.println("Invalid input");
        }
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());

            if (amount <= 0 || amount > balance) return;

            balance -= amount;
            balanceLabel.setText(String.valueOf(balance));

            withdrawField.clear();
            withdrawBox.setVisible(false);
            withdrawBox.setManaged(false);

        } catch (Exception e) {
            System.out.println("Invalid input");
        }
    }

}
