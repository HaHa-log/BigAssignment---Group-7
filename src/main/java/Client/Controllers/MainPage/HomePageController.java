package Client.Controllers.MainPage;

import Client.Controllers.AuctionPage.AuctionListController;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;


public class HomePageController{
    @FXML
    private AuctionListController auctionListController;

    @FXML
    public void initialize() {
        if (auctionListController != null) {
            System.out.println("HomePage: Refreshing mini auction view...");
            auctionListController.populateList();
        } else {
            System.err.println("HomePage: auctionListController injection failed!");
        }
    }
    @FXML
    private void toProfilePage() {
        SceneManager.switchContent("/MainFXML/ProfilePage.fxml");
    }

    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
}
