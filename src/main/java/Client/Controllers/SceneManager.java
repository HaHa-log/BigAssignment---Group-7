package Client.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static Stage stage;
    private static StackPane contentArea;

    public static void setStage(Stage stage) {
        SceneManager.stage = stage;
    }

    // New setter so the Controller can provide the Pane
    public static void setContentArea(StackPane area) {
        SceneManager.contentArea = area;
    }

    public static void loadLayout() {
        try {
            Parent shell = FXMLLoader.load(SceneManager.class.getResource("/Layout.fxml"));
            stage.setScene(new Scene(shell));
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchContent(String fxmlPath) {
        try {
            Parent node = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            if (contentArea != null) {
                contentArea.getChildren().setAll(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load Scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}