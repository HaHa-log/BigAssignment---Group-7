package Client;

import javafx.application.Application;
import javafx.stage.Stage;
import Client.Controllers.LoginPage.DemoPageController;

import java.io.IOException;

public class ClientApp extends Application {
    public void start(Stage demoStage) throws IOException {
        DemoPageController demoPage = new DemoPageController();
        demoPage.openDemoPage();
    }

}