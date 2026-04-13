package Client.Controllers.LoginPage;

import javafx.event.Event;
import javafx.fxml.FXML;

import java.io.IOException;

public class LoginController {

    private DemoPageController mainController;

    public void setMainController(DemoPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void goToRegister() throws IOException {
        mainController.showRegister();
    }

    @FXML
    private void handleLogin () {
        System.out.println("Login");
    }
}