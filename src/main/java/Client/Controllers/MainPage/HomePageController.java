package Client.Controllers.MainPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;


public class HomePageController{

    @FXML
    private void toProfilePage() {
        SceneManager.switchContent("/MainPageFXML/HomePage.fxml");
    }

    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
}
