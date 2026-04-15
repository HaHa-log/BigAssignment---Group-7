package Client.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class LayoutController {

    @FXML
    private StackPane contentArea;

    public void initialize() {
        // run automatically when Layout.fxml is loaded
        SceneManager.setContentArea(contentArea);
    }

    @FXML
    public void redirectToProfile() {
        SceneManager.switchContent("/MainFXML/ProfilePage.fxml");
    };

    @FXML
    public void redirectToHomePage() {
        SceneManager.switchContent("/MainFXML/HomePage.fxml");
    }
}