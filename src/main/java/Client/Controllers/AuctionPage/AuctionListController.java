package Client.Controllers.AuctionPage;

import Branch.Auction;
import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AuctionListController {
    @FXML
    private TilePane auctionTilePane;
    @FXML
    private Label shortcutToAuctionList;

    AuctionManager auctionManager = AuctionManager.getInstance();

    @FXML
    private void initialize() {
        this.populateList();
    }

    public void populateList() {
        auctionTilePane.getChildren().clear();
        List<Auction> auctions = auctionManager.getActiveSessions();

        if (auctions.isEmpty()) {
            System.err.println("DEBUG: No active auctions found in AuctionManager.");
            return;
        }

        for (Auction auction : auctions) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionCard.fxml"));
                VBox card = loader.load();

                AuctionCardController cardController = loader.getController();
                cardController.setAuctionData(auction);

                auctionTilePane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
