package Client;

import Client.Controllers.LoginPage.DemoPageController;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }
}