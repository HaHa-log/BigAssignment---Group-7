package Client.Controllers.AuctionPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;

public class AuctionCardController {
    @FXML
    private void toAuctionDetail() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionDetail.fxml");
    }
}
