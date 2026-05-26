package controllers.MainPage.ProfilePage;

import models.SessionManager;
import models.User;
import controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import utils.ImageFileValidator;
import services.UserApiService;

import java.io.File;
import java.io.IOException;


public class ProfilePageController {

    User user = SessionManager.getCurrentUser();

    private File selectedAvatarFile;

    private final UserApiService userApiService = new UserApiService();

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Circle avatarCircle;

    @FXML
    private TabPane tabPane;

    @FXML
    private VBox mainContent;

    @FXML
    private StackPane contentPane;
    @FXML private StackPane profilePane;
    @FXML private StackPane passwordPane;
    @FXML private StackPane financePane;
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
            loadAvatar();

            BaseController.setNavigation(this);

            loadView(profilePane,
                    "/MainFXML/ProfilePage/ProfilePane.fxml");

            loadView(passwordPane,
                    "/MainFXML/ProfilePage/PasswordPane.fxml");

            loadView(financePane,
                    "/MainFXML/ProfilePage/FinancePane.fxml");

            loadView(historyPane,
                    "/MainFXML/ProfilePage/HistoryPane.fxml");
        }
    }

    private void loadAvatar() {
        String avatarPath = user.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            String avatarUrl = userApiService.getAvatarUrl(user.getAvatarPath());

            if (avatarUrl == null) {
                avatarCircle.setFill(javafx.scene.paint.Color.GRAY);
                return;
            }

            Image img = new Image(avatarUrl, false);
            if (img.isError()) {
                avatarCircle.setFill(javafx.scene.paint.Color.GRAY);
                return;
            }

            avatarCircle.setFill(new ImagePattern(img));
        }
    }

    @FXML
    private void handleChooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        ImageFileValidator.validate(selectedAvatarFile);

        selectedAvatarFile = chooser.showOpenDialog(avatarCircle.getScene().getWindow());

        if (selectedAvatarFile == null) {
            return;
        }

        try {
            ImageFileValidator.validate(selectedAvatarFile);

            User updatedUser = userApiService.uploadAvatar(user.getId(), selectedAvatarFile);
            SessionManager.updateCurrentUser(updatedUser);
            user = updatedUser;

            loadAvatar();

        } catch (IllegalArgumentException | exceptions.ApiException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("File Error: Could not upload avatar.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private void loadView(StackPane pane, String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
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
                "/MainFXML/ProfilePage/ProfilePane.fxml");

        contentPane.getChildren().setAll(mainContent);

        tabPane.getSelectionModel().select(0);
    }

    @FXML
    private void showEditProfile() {
        loadView(contentPane, "/MainFXML/ProfilePage/EditPane.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.logoutCurrentUser();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
        SceneManager.setRememberUser(false);
    }
}
