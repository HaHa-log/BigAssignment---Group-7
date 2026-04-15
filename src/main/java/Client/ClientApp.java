package Client;

import Client.Controllers.LoginPage.DemoPageController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    public void start(Stage demoStage) throws IOException {
        DemoPageController demoPage = new DemoPageController();
        demoPage.openDemoPage();
    }

}