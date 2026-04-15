package Client.Controllers.LoginPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class DemoPageController{
    @FXML
    private StackPane ContentPane;
    private SceneManager controller;

    public void initialize() {
        try {
            showLogin();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegister() throws IOException {
        FXMLLoader registerLoad = new FXMLLoader(getClass().getResource("/LoginFXML/Register.fxml"));
        Parent registerContent = registerLoad.load();

        RegisterController controller = registerLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(registerContent);
    }

    public void showLogin() throws IOException {
        FXMLLoader loginLoad = new FXMLLoader(getClass().getResource("/LoginFXML/Login.fxml"));
        Parent loginContent = loginLoad.load();

        LoginController controller = loginLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(loginContent);
    }

    public void onSuccessfulLogin() {
        controller.switchScene("/MainFXML/HomePage.fxml");
    }
}