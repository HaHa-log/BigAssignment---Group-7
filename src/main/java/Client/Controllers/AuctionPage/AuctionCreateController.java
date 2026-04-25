package Client.Controllers.AuctionPage;

import Branch.AuctionManager;
import Branch.Item;
import Branch.Member;
import Branch.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class AuctionCreateController {
    @FXML
    private TextField itemNameInput;
    @FXML
    private TextField descriptionInput;
    @FXML
    private TextField startingPriceInput;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label auctionCreateResult;
    @FXML
    private ImageView imagePreview;

    public AuctionManager auction = AuctionManager.getInstance();
    private Member seller = (Member) SessionManager.getCurrentUser();

    @FXML
    private void createAuction() {
        String itemName = itemNameInput.getText();
        String description = descriptionInput.getText();
        Double startingPrice = Double.parseDouble(startingPriceInput.getText());

        Item item = new Item(itemName, startingPrice, description);
        auction.createAuction(seller, item, LocalDateTime.now(), null);

        if (itemName == null || startingPrice == null) {
            auctionCreateResult.setTextFill(RED);
            auctionCreateResult.setText("Please fill all the fields.");
        } else {
            auctionCreateResult.setTextFill(GREEN);
            auctionCreateResult.setText("Auction created.");
        }
    }

    //Will display image but only on the owner's device
    //Have to save the image into database to display for other users
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            fileNameLabel.setText(selectedImageFile.getName());
            Image image = new Image(selectedImageFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }
}
