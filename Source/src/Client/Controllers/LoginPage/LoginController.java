package Client.Controllers.LoginPage;

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
}
