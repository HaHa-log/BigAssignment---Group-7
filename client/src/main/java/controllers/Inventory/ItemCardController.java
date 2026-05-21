package controllers.Inventory;

import models.Auction;
import models.AuctionManager;
import models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

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

    private Item item;

    /*
    public void setItemData(Item item) {
        this.item = item;
        this.statusLabel.setText(item.getStatus().toString());
        this.itemNameLabel.setText(item.getName());
        this.startingPriceLabel.setText("Starting price: $" + item.getStartingPrice());
        if (AuctionManager.getInstance().getAuctionByItem(item) ==  null) {
            this.currentPriceLabel.setText("Current price: $" + item.getStartingPrice());
        } else {
            this.currentPriceLabel.setText("Current price: $" + AuctionManager.getInstance().getAuctionByItem(item).getCurrentPrice());
        }

        setItemImage();
    }

    private void setItemImage() {
        File file = new File("src/main/resources/ItemImages/" + item.getImagePath());
        Image image = new Image(file.toURI().toString());

        imageContainer.setImage(image);
    }

     */
}

