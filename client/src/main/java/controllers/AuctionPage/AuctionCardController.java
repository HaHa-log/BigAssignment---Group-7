package controllers.AuctionPage;

import models.Auction;
import controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class AuctionCardController {
    @FXML
    private Label statusLabel;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label startingPriceLabel;
    @FXML
    private Label currentPriceLabel;
    @FXML
    private ImageView imageContainer;

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
        this.statusLabel.setText(auction.getStatus().toString());
        this.itemNameLabel.setText(auction.getItem().getName());
        this.startingPriceLabel.setText("Starting price: $" + auction.getStartingPrice());
        this.currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());

        setItemImage();
    }

    private void setItemImage() {
        String imagePath = auction.getItem().getImagePath();

        if (imagePath == null || imagePath.isBlank() || "null".equalsIgnoreCase(imagePath)) {
            imageContainer.setImage(null);
            return;
        }

        String imageUrl = config.ApiConfig.baseUrl() + "/api/items/images/" + imagePath;
        Image image = new Image(imageUrl, true);
        imageContainer.setImage(image);
    }
}
