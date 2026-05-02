package Client.Controllers.MainPage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static javafx.scene.paint.Color.*;

public class ProfilePageController {

    User user = SessionManager.getCurrentUser();

    @FXML
    private StackPane contentPane;

    // PANES
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
    private PasswordField oldPasswordField, newPasswordField, newPasswordConfirmField;

    // FINANCE
    @FXML
    private VBox depositBox, withdrawBox;
    @FXML
    private TextField depositField, withdrawField;
    @FXML
    private Label balanceLabel, financeStatus, depositStatus, withdrawStatus;

    // STATUS
    @FXML
    private Label passwordChangeStatus;

    // STYLE
    private final String ACTIVE = "-fx-text-fill: #38bdf8; -fx-font-weight: bold; -fx-cursor: hand;";
    private final String NORMAL = "-fx-text-fill: #475569; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        if (user != null) {
            loadUserData();
            showProfile();
        }
    }

    private VBox loadSubPane(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(this);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void switchView(String fxmlPath) {
        VBox pane = loadSubPane(fxmlPath);
        if (pane != null) {
            contentPane.getChildren().setAll(pane);
        }
    }

    private void loadUserData() {

        usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
        phoneLabel.setText(user.getPhoneNumber());
        emailLabel.setText(user.getEmail());
        aboutLabel.setText("Not yet set");
        if (user.isAdmin()) {
            roleLabel.setText("Admin");
        } else {
            roleLabel.setText("Member");
        }
    }

    private void setActive(Label tab) {
        tabProfile.setStyle(NORMAL);
        tabPassword.setStyle(NORMAL);
        tabFinance.setStyle(NORMAL);
        tabNotification.setStyle(NORMAL);
        tabHistory.setStyle(NORMAL);
        if (tab != null) tab.setStyle(ACTIVE);
    }

    @FXML
    private void showProfile() {
        switchView("/MainFXML/Profile/ProfilePane.fxml");
        loadUserData();
        setActive(tabProfile);
    }

    @FXML
    private void showEditProfile() {
        switchView("/MainFXML/Profile/EditPane.fxml");
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
        switchView("/MainFXML/Profile/PasswordPane.fxml");
        setActive(tabPassword);
    }

    @FXML
    private void showFinance() {
        switchView("/MainFXML/Profile/FinancePane.fxml");
        if (balanceLabel != null) {
            balanceLabel.setText("" + user.getBalance());
        }
        setActive(tabFinance);
    }

    @FXML
    private void showNotification() {
        switchView("/MainFXML/Profile/NotificationPane.fxml");
        setActive(tabNotification);
    }

    @FXML
    private void showHistory() {
        switchView("/MainFXML/Profile/HistoryPane.fxml");
        setActive(tabHistory);
    }

    @FXML
    public void handleSave(ActionEvent event) {
        user.setEmail(emailField.getText());
        user.setPhoneNumber(phoneField.getText());
        showProfile();
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }

    @FXML
    public void handleChangePassword(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String newPasswordConfirm = newPasswordConfirmField.getText();

        passwordChangeStatus.setTextFill(RED);
        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.equals(newPasswordConfirm) && !newPassword.isEmpty()) {
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
            Double amount = Double.parseDouble(depositField.getText());
            user.depositMoney(amount);
            depositStatus.setTextFill(GREEN);
            depositStatus.setText("Deposited " + amount + " successfully!");
            balanceLabel.setText("" + user.getBalance());
        } catch (Exception e) {
            depositStatus.setTextFill(RED);
            depositStatus.setText("Invalid amount");
        }
        depositField.clear();
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            Double amount = Double.parseDouble(withdrawField.getText());
            user.withdrawMoney(amount);
            withdrawStatus.setTextFill(GREEN);
            withdrawStatus.setText("Withdrawn " + amount + " successfully!");
            balanceLabel.setText("" + user.getBalance());
        } catch (Exception e) {
            withdrawStatus.setTextFill(RED);
            withdrawStatus.setText("Invalid amount or insufficient funds");
        }
        withdrawField.clear();
    }
}