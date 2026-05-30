package controllers.Inventory;

import models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.ItemApiService;

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
    private final ItemApiService itemApiService = new ItemApiService();

    public void setItemData(Item item, double displayPrice) {
        this.item = item;

        this.statusLabel.setText(item.getStatus().toString());
        this.itemNameLabel.setText(item.getName());
        this.startingPriceLabel.setText("Starting price: $" + item.getStartingPrice());

        updateCurrentPrice(displayPrice);

        setItemImage();
    }

    public void updateCurrentPrice(double newPrice) {
        javafx.application.Platform.runLater(() -> {
            this.currentPriceLabel.setText("Current price: $" + newPrice);
        });
    }

    public Item getItem() {
        return this.item;
    }

    private void setItemImage() {
        String imagePath = item.getImagePath();

        if (imagePath == null || imagePath.isBlank() || "null".equalsIgnoreCase(imagePath)) {
            imageContainer.setImage(null);
            return;
        }

        String imageUrl = itemApiService.getItemImageUrl(imagePath);
        imageContainer.setImage(new Image(imageUrl, true));
    }
}