package Client.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class LayoutController {

    @FXML
    private StackPane contentArea;

    public void initialize() {
        // run automatically when Layout.fxml is loaded
        SceneManager.setContentArea(contentArea);
    }

    @FXML
    public void redirectToProfile() {
        SceneManager.switchContent("/MainFXML/Profile/ProfileMain.fxml");
    }

    @FXML
    public void redirectToHomePage() {
        SceneManager.switchContent("/MainFXML/HomePage.fxml");
    }

    @FXML
    public void toAuctionCreate() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionCreate.fxml");
    }

    @FXML
    public void refreshPage() {
        String currentPath = SceneManager.getCurrentContent();

        if (currentPath != null) {
            SceneManager.switchContent(currentPath);
        }
    }
}