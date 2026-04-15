package Client;

import Branch.AuthService;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AuthService.registerNewUser("Bui", "Ha", "buihalinh@gmail.com", "0835361207", "060108");
        SceneManager.setStage(stage);
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }
}