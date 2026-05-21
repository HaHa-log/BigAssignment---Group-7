package app;

import controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);
        SceneManager.startApp();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
