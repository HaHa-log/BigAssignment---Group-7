package Client;

import Branch.*;
import Client.Controllers.SceneManager;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.ItemsDAO;
import model.UsersDAO;
import model.impl.DaoFactory;
import model.impl.UsersDAOImpl;

import java.time.LocalDateTime;
import java.util.List;

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