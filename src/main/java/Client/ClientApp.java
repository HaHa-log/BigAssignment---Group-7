package Client;

import Branch.AuthService;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AuthService.registerNewUser("Admin", "123", "admin@gmail.com", "0123456789", "000000");
        SceneManager.setStage(stage);
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }
}