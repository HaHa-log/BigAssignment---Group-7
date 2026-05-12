package Client.Controllers.MainPage.ProfilePage;

import Branch.SessionManager;
import Branch.User;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
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
    private TabPane tabPane;

    @FXML
    private StackPane contentPane;
    @FXML private StackPane profilePane;
    @FXML private StackPane passwordPane;
    @FXML private StackPane financePane;
    @FXML private StackPane notificationPane;
    @FXML private StackPane historyPane;

    @FXML
    public void initialize() {
        if (user != null) {
            usernameLabel.setText(user.getFullName());
            if (user.isAdmin()) {
              roleLabel.setText("Admin");
            } else {
              roleLabel.setText("User");
            }

            BaseController.setNavigation(this);

            loadView(profilePane,
                    "/MainFXML/Profile/ProfilePane.fxml");

            loadView(passwordPane,
                    "/MainFXML/Profile/PasswordPane.fxml");

            loadView(financePane,
                    "/MainFXML/Profile/FinancePane.fxml");

            loadView(notificationPane,
                    "/MainFXML/Profile/NotificationPane.fxml");

            loadView(historyPane,
                    "/MainFXML/Profile/HistoryPane.fxml");
        }
    }

    private void loadView(StackPane pane, String fxmlPath) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(fxmlPath));

            VBox view = loader.load();

            Object controller = loader.getController();

            if (controller instanceof BaseController base) {
                base.setUser(user);
            }

            pane.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProfile() {

        loadView(profilePane,
                "/MainFXML/Profile/ProfilePane.fxml");

        tabPane.getSelectionModel().select(0);
    }

    @FXML
    private void showEditProfile() {
        loadView(profilePane,"/MainFXML/Profile/EditPane.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }
}
