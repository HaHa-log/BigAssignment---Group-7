package Client.Controllers.MainPage.ProfilePage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProfilePageController {

    User user = SessionManager.getCurrentUser();

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private Label tabProfile, tabPassword,tabNotification, tabFinance, tabHistory;

    private final String ACTIVE = "-fx-text-fill: #38bdf8; -fx-font-weight: bold;";
    private final String NORMAL = "-fx-text-fill: #475569;";

    @FXML
    public void initialize() {
        if (user != null) {
            usernameLabel.setText(user.getFullName());
            if (user.isAdmin()) {
              roleLabel.setText("Admin");
            } else {
              roleLabel.setText("User");
            }

            showProfile();
            BaseController.setNavigation(this);
        }
    }

    private void switchView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            VBox pane = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController base) {
                base.setUser(user);
            }

            contentPane.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
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
    void showProfile() {
        switchView("/MainFXML/Profile/ProfilePane.fxml");
        setActive(tabProfile);
    }

    @FXML
    private void showEditProfile() {
        switchView("/MainFXML/Profile/EditPane.fxml");
    }

    @FXML
    private void showChangePassword() {
        switchView("/MainFXML/Profile/PasswordPane.fxml");
        setActive(tabPassword);
    }

    @FXML
    private void showFinance() {
        switchView("/MainFXML/Profile/FinancePane.fxml");
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
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }
}
