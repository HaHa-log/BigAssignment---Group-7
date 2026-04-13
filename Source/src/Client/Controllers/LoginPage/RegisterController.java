package Client.Controllers.LoginPage;

import javafx.fxml.FXML;

import java.io.IOException;

public class RegisterController {

    private DemoPageController mainController;

    public void setMainController(DemoPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void goToLogin() throws IOException {
        mainController.showLogin();
    }
}
