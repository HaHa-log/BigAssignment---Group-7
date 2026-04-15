package Client.Controllers.MainPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;

public class HomePageController{
    @FXML
    private void redirectToProfile() {
        SceneManager.switchScene("/MainFXML/ProfilePage.fxml");
    }
}
