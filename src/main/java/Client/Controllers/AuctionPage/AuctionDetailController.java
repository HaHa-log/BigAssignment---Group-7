package Client.Controllers.AuctionPage;

import Branch.Auction;
import Branch.SessionManager;
import Branch.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuctionDetailController {
    @FXML
    private Label statusLabel;
    @FXML
    private TextField bidAmountInput;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label startingPriceLabel;
    @FXML
    private Label currentPriceLabel;

    private Bidder currentUser = (Bidder) SessionManager.getCurrentUser();
    private Auction auction;

    @FXML
    private void checkBidValidity() {
        String bidAmountString = bidAmountInput.getText();

        if (bidAmountString == null || bidAmountString.trim().isEmpty()) {
            statusLabel.setText("Please enter an amount.");
        }

        try {
            double bidAmount = Double.parseDouble(bidAmountString);
            boolean isValid = auction.placeBid(currentUser, bidAmount);

            if (isValid) {
                statusLabel.setText("Bid placed successfully!");
                bidAmountInput.clear();
            } else {
                statusLabel.setText("Bid too low or auction closed.");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format.");
        }
    }

    public void setAuctionData(Auction auction) {
        this.auction = auction;
        this.itemNameLabel.setText(auction.getItem().getName());
        this.startingPriceLabel.setText("Starting price: $" + auction.getStartingPrice());
        this.currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());
    }
}
