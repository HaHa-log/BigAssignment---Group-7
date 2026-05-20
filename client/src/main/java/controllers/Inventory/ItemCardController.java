package controllers.Inventory;

import models.Auction;
import models.Item;
import controllers.AuctionPage.AuctionDetailController;
import controllers.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;

public class ItemCardController {
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
            String fxmlPath = "/AuctionPageFXML/AuctionDetail.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionDetail.fxml"));
            Parent detailRoot = loader.load();

            AuctionDetailController controller = loader.getController();

            //controller.setAuctionData(this.auction);
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
        Item item = auction.getItem();
        String filePath = item.getImagePath();

        File file = new File("src/main/resources/ItemImages/" + item.getImagePath());
        Image image = new Image(file.toURI().toString());

        imageContainer.setImage(image);
    }
}

