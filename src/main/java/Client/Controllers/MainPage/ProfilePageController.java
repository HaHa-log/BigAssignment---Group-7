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

    @FXML private VBox profilePane, editPane, passwordPane, notificationPane, historyPane;

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
    }

    @FXML
    private void showEditProfile() {
        hideAll();
        editPane.setVisible(true);
    }

    @FXML
    private void cancelEdit() {
        showProfile();
    }

    @FXML
    private void showChangePassword() {
        hideAll();
        passwordPane.setVisible(true);
    }

    @FXML
    private void showNotification() {
        hideAll();
        notificationPane.setVisible(true);
    }

    @FXML
    private void showHistory() {
        hideAll();
        historyPane.setVisible(true);
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }

    public void handleSave(ActionEvent actionEvent) {
    }
}
