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

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class ProfilePageController {

    User user = SessionManager.getCurrentUser();

    private File selectedAvatarFile;

    private static final String AVATAR_DIR =
            "src/main/resources/Avatars";

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Circle avatarCircle;

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
            loadAvatar();

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

    private void loadAvatar() {
        String avatarPath = user.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File file = new File(AVATAR_DIR + "/" + avatarPath);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());

                // ImagePattern giúp ảnh tự động lấp đầy hình tròn và CĂN GIỮA
                ImagePattern pattern = new ImagePattern(img);
                avatarCircle.setFill(pattern);
            }
        } else {
            // Ảnh mặc định nếu chưa có avatar
            avatarCircle.setFill(javafx.scene.paint.Color.GRAY);
        }
    }

    @FXML
    private void handleChooseAvatar() {
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        selectedAvatarFile = chooser.showOpenDialog(avatarCircle.getScene().getWindow());

        if (selectedAvatarFile != null) {
            try {
                processAvatarUpload();

                Image img = new Image(selectedAvatarFile.toURI().toString());

                avatarCircle.setFill(new javafx.scene.paint.ImagePattern(img));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processAvatarUpload()
            throws IOException {

        File dir = new File(AVATAR_DIR);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName =
                "avatar_" +
                        user.getId() +
                        "_" +
                        System.currentTimeMillis() +
                        ".png";

        File destFile =
                new File(dir, fileName);

        Files.copy(
                selectedAvatarFile.toPath(),
                destFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        user.setAvatarPath(fileName);

        user.update();
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
