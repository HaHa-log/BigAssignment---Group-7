package Client.Controllers.AuctionPage;

import Branch.AuctionManager;
import Branch.Item;
import Branch.Member;
import Branch.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private DatePicker startingDateInput, endingDateInput;
    @FXML
    private TextField startingTimeInput, endingTimeInput;
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

        LocalDate startDate = startingDateInput.getValue();
        LocalDate endDate = endingDateInput.getValue();

        String startingTime = startingTimeInput.getText();
        String endingTime = endingTimeInput.getText();

        if (itemName == null || startingPrice == null) {
            auctionCreateResult.setTextFill(RED);
            auctionCreateResult.setText("Please fill all the fields.");
        } else if (startDate == null || endDate == null || startingTime == null || endingTime == null) {
            auctionCreateResult.setTextFill(RED);
            auctionCreateResult.setText("Please choose starting and ending time.");
        }
        else {
            try {
                LocalTime startTime = LocalTime.parse(startingTime);
                LocalTime endTime = LocalTime.parse(endingTime);

                LocalDateTime startFull = startDate.atTime(startTime);
                LocalDateTime endFull = endDate.atTime(endTime);

                Item item = new Item(itemName, startingPrice, description);
                item.saveItem();
                auction.createAuction(seller, item, startFull, endFull);
                auctionCreateResult.setTextFill(GREEN);
                auctionCreateResult.setText("Auction created.");
            } catch (IllegalArgumentException e) {
                String message = e.getMessage();
                auctionCreateResult.setTextFill(GREEN);
                auctionCreateResult.setText(message);
            }
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
