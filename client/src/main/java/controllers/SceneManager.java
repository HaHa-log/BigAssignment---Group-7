package controllers;

//preferences API

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;

public class SceneManager {
    private static Stage stage;
    private static StackPane contentArea;

    private static String currentContentPath;

    private static Preferences prefs = Preferences.userNodeForPackage(SceneManager.class);
    private static final String REMEMBER_KEY = "rememberUser";

    public static void setStage(Stage stage) {
        SceneManager.stage = stage;
        setupStage();
    }

    // New setter so the Controller can provide the Pane
    public static void setContentArea(StackPane area) {
        SceneManager.contentArea = area;
    }

    //setup Icons and Exit
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

        stage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit();
        });
    }

    //Load general layout
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
        try {
            Parent node = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            currentContentPath = fxmlPath;
            try {
                contentArea.getChildren().setAll(node);
            } catch (NullPointerException e) {
                System.err.println("Location is null!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchContent(Parent node) {
        try{
            contentArea.getChildren().setAll(node);
        } catch (NullPointerException e) {
            System.err.println("Location is null!");
        }
    }

    public static void switchScene(String fxmlPath) {
        try {
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

    //RememberMe
    public static void setRememberUser(boolean value) {
        prefs.putBoolean(REMEMBER_KEY, value);
    }

    public static boolean userIsRemembered() {
        return prefs.getBoolean(REMEMBER_KEY, false);
    }

    public static void startApp() {
        switchScene("/LoginFXML/DemoPage.fxml");
    }

    public static String getCurrentContent() {
        return currentContentPath;
    }
}
