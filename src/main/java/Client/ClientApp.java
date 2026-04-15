package Client;

import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }
}