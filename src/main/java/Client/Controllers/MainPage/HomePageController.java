package Client.Controllers.MainPage;

import Branch.SessionManager;
import Client.Controllers.AuctionPage.AuctionListController;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class HomePageController{
    @FXML
    private AuctionListController auctionListController;
    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        String userName =SessionManager.getCurrentUser().getName();
        welcomeLabel.setText("Welcome, " + userName);
    }
    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
}
