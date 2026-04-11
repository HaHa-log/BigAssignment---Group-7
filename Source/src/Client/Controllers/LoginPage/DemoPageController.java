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

    public void openDemoPage() throws IOException {
        FXMLLoader demoPage = new FXMLLoader(getClass().getResource("/Client/resources/DemoPage.fxml"));
        Parent demoPageRoot = demoPage.load();
        Scene demoPageScene = new Scene(demoPageRoot);

        Stage demoPageStage = new Stage();
        demoPageStage.setFullScreen(true);
        demoPageStage.setScene(demoPageScene);
        demoPageStage.show();
    }

    public void showRegister() throws IOException {
        FXMLLoader registerLoad = new FXMLLoader(getClass().getResource("/Client/resources/Register.fxml"));
        Parent registerContent = registerLoad.load();

        RegisterController controller = registerLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(registerContent);
    }

    public void showLogin() throws IOException {
        FXMLLoader loginLoad = new FXMLLoader(getClass().getResource("/Client/resources/Login.fxml"));
        Parent loginContent = loginLoad.load();

        RegisterController controller = loginLoad.getController();
        controller.setMainController(this);

        ContentPane.getChildren().setAll(loginContent);
    }
}