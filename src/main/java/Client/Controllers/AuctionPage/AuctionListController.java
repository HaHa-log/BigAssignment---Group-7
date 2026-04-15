package Client.Controllers.AuctionPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AuctionListController {
    @FXML
    private TilePane auctionTilePane;

    public void initialize() {
        //4 cards
        for (int i = 0; i < 4; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/AuctionCard.fxml"));
                VBox card = loader.load();

                // Add the card to the TilePane
                auctionTilePane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
