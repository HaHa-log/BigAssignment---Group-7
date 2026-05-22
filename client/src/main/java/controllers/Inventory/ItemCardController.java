package controllers.Inventory;

import models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

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

    public void setItemData(Item item, double displayPrice) {
        this.item = item;

        this.statusLabel.setText(item.getStatus().toString());
        this.itemNameLabel.setText(item.getName());
        this.startingPriceLabel.setText("Starting price: $" + item.getStartingPrice());

        this.currentPriceLabel.setText("Current price: $" + displayPrice);

        setItemImage();
    }

    private void setItemImage() {
        if (item.getImagePath() == null || item.getImagePath().trim().isEmpty()) {
            return;
        }

        try {
            String fullPath = "/ItemImages/" + item.getImagePath();
            InputStream is = getClass().getResourceAsStream(fullPath);

            if (is != null) {
                imageContainer.setImage(new Image(is));
            } else {
                System.err.println("Could not find image resource file: " + fullPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to load item image: " + e.getMessage());
        }
    }
}