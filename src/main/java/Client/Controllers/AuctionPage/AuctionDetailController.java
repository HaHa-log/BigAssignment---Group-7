package Client.Controllers.AuctionPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;

public class AuctionDetailController {
    @FXML
    private void toAuctionDetail() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionDetail.fxml");
    }
}
