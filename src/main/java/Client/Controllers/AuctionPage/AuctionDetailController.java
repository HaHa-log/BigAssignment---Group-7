package Client.Controllers.AuctionPage;

import Branch.Auction;
import Branch.Exceptions.CustomisedException;
import Branch.SessionManager;
import Branch.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

import static javafx.scene.paint.Color.*;

public class AuctionDetailController {
    @FXML
    private Label bidPlacedResultLabel;
    @FXML
    private TextField bidAmountInput;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label startingPriceLabel;
    @FXML
    private Label currentPriceLabel;
    @FXML
    private Label ownerNameLabel, itemDescriptionLabel, auctionStatusLabel, durationLabel;
    @FXML
    private ImageView imageContainer;

    private Bidder currentUser = (Bidder) SessionManager.getCurrentUser();
    private Auction auction;

    @FXML
    private void checkBidValidity() {
        String bidAmountString = bidAmountInput.getText();

        if (bidAmountString == null || bidAmountString.trim().isEmpty()) {
            bidPlacedResultLabel.setText("Please enter an amount.");
        }

        try {
            double bidAmount = Double.parseDouble(bidAmountString);
            boolean isSuccess = currentUser.placeBid(auction, bidAmount);

            if (isSuccess) {
                bidPlacedResultLabel.setTextFill(GREEN);
                bidPlacedResultLabel.setText("Bid placed successfully.");
                currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());

            } else {
                bidPlacedResultLabel.setTextFill(RED);
                bidPlacedResultLabel.setText("Bid failed. Check your balance or bid amount.");
            }

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            bidPlacedResultLabel.setText(message);
        } catch (CustomisedException e){
            String message = e.getMessage();
            bidPlacedResultLabel.setText(message);
        } finally {
            bidAmountInput.clear();
        }
    }

    public void setAuctionData(Auction auction) {
        this.auction = auction;
        this.itemNameLabel.setText(auction.getItem().getName());
        this.startingPriceLabel.setText("Starting price: $" + auction.getStartingPrice());
        this.currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());

        setItemImage();
        getTableData(auction);
    }

    public void getTableData(Auction auction) {
        ownerNameLabel.setText(auction.getOwner().getFullName());
        itemDescriptionLabel.setText(auction.getItem().getDescription());
        auctionStatusLabel.setText(auction.getStatus().toString());

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm");

        String startStr = auction.getStartingTime().format(formatter);
        String endStr = auction.getEndingTime().format(formatter);

        durationLabel.setText(startStr + " - " + endStr);
    }

    private void setItemImage() {
        Item item = auction.getItem();
        String filePath = item.getImagePath();

        File file = new File("src/main/resources/ItemImages/" + filePath);
        Image image = new Image(file.toURI().toString());

        imageContainer.setImage(image);
    }
}
