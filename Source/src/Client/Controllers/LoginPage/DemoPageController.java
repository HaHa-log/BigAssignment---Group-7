package Client.Controllers.LoginPage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class DemoPageController{
    public void openDemoPage() throws IOException {
        FXMLLoader demoPage = new FXMLLoader(getClass().getResource("/Client/resources/DemoPage.fxml"));
        Parent demoPageRoot = demoPage.load();
        Scene demoPageScene = new Scene(demoPageRoot);

        Stage demoPageStage = new Stage();
        demoPageStage.setFullScreen(true);
        demoPageStage.setScene(demoPageScene);
        demoPageStage.show();
    }
}
