package app;

import controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);
        //SceneManager.startApp();
        SceneManager.switchScene("/LoginFXML/DemoPage.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}