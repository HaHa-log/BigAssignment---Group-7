package controllers;

import exceptions.ApiException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.SessionManager;
import models.User;
import services.UserApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class SceneManager {
    private static Stage stage;
    private static StackPane contentArea;
    private static String currentContentPath;
    private static final Preferences prefs = Preferences.userNodeForPackage(SceneManager.class);
    private static final String REMEMBER_KEY = "rememberUser";
    private static final UserApiService userApiService = new UserApiService();

    private static final Map<String, Parent> viewCache = new HashMap<>();

    public static void setStage(Stage stage) {
        SceneManager.stage = stage;
        setupStage();
    }

    public static void setContentArea(StackPane area) {
        SceneManager.contentArea = area;
    }

    private static void setupStage() {
        try {
            var iconStream = SceneManager.class.getResourceAsStream("/logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Warning: logo.png not found in resources!");
            }
        } catch (Exception e) {
            System.err.println("Failed to load app icon.");
        }

        stage.setTitle("Hệ thống Đấu giá");
        stage.setOnCloseRequest(event -> javafx.application.Platform.exit());
    }

    public static void loadLayout() {
        try {
            Parent shell = FXMLLoader.load(SceneManager.class.getResource("/Layout.fxml"));
            stage.setScene(new Scene(shell));
            stage.setMinHeight(600);
            stage.setMinWidth(1000);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchContent(String fxmlPath) {
        if (contentArea == null) {
            System.err.println("Error: contentArea is null! Cần cấu hình setContentArea trước.");
            return;
        }

        try {
            Parent targetNode;

            if (viewCache.containsKey(fxmlPath)) {
                targetNode = viewCache.get(fxmlPath);
            } else {
                System.out.println("[SceneManager] Khởi tạo giao diện lần đầu: " + fxmlPath);
                targetNode = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
                viewCache.put(fxmlPath, targetNode);

                contentArea.getChildren().add(targetNode);
            }

            currentContentPath = fxmlPath;

            for (javafx.scene.Node node : contentArea.getChildren()) {
                if (node == targetNode) {
                    node.setVisible(true);
                    node.toFront();
                } else {
                    node.setVisible(false); // Ẩn ngầm các trang còn lại nhưng KHÔNG xóa khỏi RAM
                }
            }

        } catch (IOException e) {
            System.err.println("Không thể load file FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void switchContent(Parent node) {
        try {
            if (!contentArea.getChildren().contains(node)) {
                contentArea.getChildren().add(node);
            }
            node.setVisible(true);
            node.toFront();

            for (javafx.scene.Node child : contentArea.getChildren()) {
                if (child != node) {
                    child.setVisible(false);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Location is null!");
        }
    }

    public static void switchScene(String fxmlPath) {
        try {
            clearViewCache();

            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            stage.setScene(new Scene(root));
            if (fxmlPath.contains("DemoPage")) {
                stage.setFullScreen(false);
                stage.setMinHeight(700);
                stage.setWidth(800);
                stage.setResizable(true);
            } else {
                stage.setMaximized(true);
                stage.setResizable(true);
            }
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load Scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void clearViewCache() {
        viewCache.clear();
        if (contentArea != null) {
            contentArea.getChildren().clear();
        }
        System.out.println("[SceneManager] Đã dọn dẹp sạch sẽ bộ nhớ đệm View Cache.");
    }

    public static void setRememberUser(boolean value) {
        prefs.putBoolean(REMEMBER_KEY, value);
    }

    public static boolean userIsRemembered() {
        return prefs.getBoolean(REMEMBER_KEY, false);
    }

    public static void startApp() throws IOException, InterruptedException {
        if (SceneManager.userIsRemembered()) {
            String savedEmail = SessionManager.getSavedEmail();

            if (savedEmail != null) {
                try {
                    User member = userApiService.getByEmail(savedEmail);
                    SessionManager.loginCurrentUser(member);
                    SceneManager.loadLayout();
                    SceneManager.switchContent("/MainFXML/HomePage/HomePage.fxml");
                    return;
                } catch (ApiException e) {
                    System.err.println("Auto-login failed due to server error: " + e.getMessage());
                    SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
                    prefs.putBoolean(REMEMBER_KEY, false);
                }
            }
        }
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }

    public static String getCurrentContent() {
        return currentContentPath;
    }
}