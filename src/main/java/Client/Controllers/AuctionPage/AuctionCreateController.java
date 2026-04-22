package Client.Controllers.AuctionPage;

import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AuctionCreateController {
    @FXML
    private TextField itemNameInput;
    @FXML
    private TextField descriptionInput;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ImageView imagePreview;

    public AuctionManager auction = AuctionManager.getInstance();

    @FXML
    private void createAuction() {
        String itemName = itemNameInput.getText();
        String description = descriptionInput.getText();
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
