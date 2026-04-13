package Client.Controllers.LoginPage;

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

    public void initialize() {
        try {
            showLogin();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDemoPage() throws IOException {
        FXMLLoader demoPage = new FXMLLoader(getClass().getResource("/Client/resources/LoginFXML/DemoPage.fxml"));
        Parent demoPageRoot = demoPage.load();
        Scene demoPageScene = new Scene(demoPageRoot);

        Stage demoPageStage = new Stage();
        demoPageStage.setResizable(false);
        demoPageStage.setScene(demoPageScene);
        demoPageStage.show();
    }

    public void showRegister() throws IOException {
        FXMLLoader registerLoad = new FXMLLoader(getClass().getResource("/Client/resources/LoginFXML/Register.fxml"));
        Parent registerContent = registerLoad.load();

        RegisterController controller = registerLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(registerContent);
    }

    public void showLogin() throws IOException {
        FXMLLoader loginLoad = new FXMLLoader(getClass().getResource("/Client/resources/LoginFXML/Login.fxml"));
        Parent loginContent = loginLoad.load();

        LoginController controller = loginLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(loginContent);
    }
}