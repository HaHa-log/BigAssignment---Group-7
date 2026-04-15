package Client.Controllers.MainPage;

import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomePageController{
    @FXML
    private TilePane auctionTilePane;

    @FXML
    private void initialize() {
        loadLimitedAuctions();
    }

    private void loadLimitedAuctions() {
        for (int i = 0; i < 4; i ++) {
            try {
                FXMLLoader cardLoad = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionCard.fxml"));
                VBox auctionCard = cardLoad.load();

                auctionTilePane.getChildren().add(auctionCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void toAuctionList() {
        SceneManager.switchContent("/AuctionPageFXML/AuctionList.fxml");
    }
}
