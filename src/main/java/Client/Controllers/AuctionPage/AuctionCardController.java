package Client.Controllers.AuctionPage;

import Branch.Auction;
import Client.Controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class AuctionCardController {
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label startingPriceLabel;
    @FXML
    private Label currentPriceLabel;

    private Auction auction;

    @FXML
    private void toAuctionDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionDetail.fxml"));
            Parent detailRoot = loader.load();

            AuctionDetailController controller = loader.getController();

            controller.setAuctionData(this.auction);
            SceneManager.switchContent(detailRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuctionData(Auction auction) {
        this.auction = auction;
        this.itemNameLabel.setText(auction.getItem().getName());
        this.startingPriceLabel.setText("Starting price: $" + auction.getStartingPrice());
        this.currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());
    }
}
