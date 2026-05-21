package controllers.AuctionPage;

import models.*;
import models.Exceptions.CustomisedException;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import models.services.AuctionApiService;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

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
    @FXML
    private LineChart<String, Number> bidHistoryChart;
    @FXML
    private VBox confirmPane;
    @FXML
    private TextField maxBidInput;
    @FXML
    private TextField stepInput;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final AuctionApiService auctionApiService = new AuctionApiService();

    private User currentUser = SessionManager.getCurrentUser();
    private Auction auction;

    @FXML
    private void checkBidValidity() {
        String bidAmountString = bidAmountInput.getText();

        if (bidAmountString == null || bidAmountString.trim().isEmpty()) {
            bidPlacedResultLabel.setText("Please enter an amount.");
            return;
        }

        try {
            currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalArgumentException("[Error]: Session expired! Please log in again.");
            }
            double bidAmount = Double.parseDouble(bidAmountString);
            auction = auctionApiService.placeBid(auction.getId(), currentUser.getId(), bidAmount);
            boolean isSuccess = true;

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
        } catch (CustomisedException e) {
            String message = e.getMessage();
            bidPlacedResultLabel.setText(message);
        } catch (Exception e) {
            bidPlacedResultLabel.setTextFill(RED);
            bidPlacedResultLabel.setText(e.getMessage());
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
        updateBidChart();
        if (currentUser != null && currentUser.isWinner(auction)
                && auction.getRawStatus() == Auction.AuctionStatus.FINISHED){
            setupConfirmPane();
        }
    }

    public void getTableData(Auction auction) {
        ownerNameLabel.setText(auction.getOwner().getFullName());
        itemDescriptionLabel.setText(auction.getItem().getDescription());
        auctionStatusLabel.setText(auction.getStatus().toString());

        String startStr = auction.getStartingTime().format(dateTimeFormatter);
        String endStr = auction.getEndingTime().format(dateTimeFormatter);

        durationLabel.setText(startStr + " - " + endStr);
    }
    private void setItemImage() {
        Item item = auction.getItem();
        String filePath = item.getImagePath();

        if (filePath == null || filePath.isBlank()) {
            return;
        }

        File file = new File("src/main/resources/ItemImages/" + filePath);
        Image image = new Image(file.toURI().toString());

        imageContainer.setImage(image);
    }

    private void updateBidChart() {
        if (auction == null) return;

        List<Bid> bids = auction.getBids();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Price History ($)");

        for (Bid bid : bids) {
            //return time if is not null, N/A if null
            String time = (bid.getBidTime() != null)
                    ? bid.getBidTime().format(dateTimeFormatter)
                    : "N/A";
            series.getData().add(new XYChart.Data<>(time, bid.getBidPrice().getPrice()));
        }

        bidHistoryChart.getData().clear();
        bidHistoryChart.getData().add(series);

        bidHistoryChart.setAnimated(false);
    }

    public void setupAuction() {
        auction.addObserver((AuctionObserver) SessionManager.getCurrentUser());
    }

    private void setupConfirmPane() {
        confirmPane.setVisible(true);
        confirmPane.setManaged(true);
    }

    @FXML
    private void confirm() {
        try {
            currentUser = SessionManager.getCurrentUser();
            if (!(currentUser instanceof Member member)) {
                throw new IllegalArgumentException("[Error]: Only members can confirm receipt.");
            }
            auction = auctionApiService.confirmReceipt(auction.getId(), member.getId());
            auctionStatusLabel.setText(auction.getRawStatus().toString());
            confirmPane.setVisible(false);
            confirmPane.setManaged(false);

            bidPlacedResultLabel.setTextFill(GREEN);
            bidPlacedResultLabel.setText("Receipt confirmed. Transaction completed.");
        } catch (Exception e) {
            bidPlacedResultLabel.setTextFill(RED);
            bidPlacedResultLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void checkAutoBidValidity() {
        if (maxBidInput.getText().isEmpty() || stepInput.getText().isEmpty()) {
            bidPlacedResultLabel.setTextFill(RED);
            bidPlacedResultLabel.setText("Please enter Max Bid and Increment.");
            return;
        }
        try {
            double maxBid = Double.parseDouble(maxBidInput.getText());
            double increment = Double.parseDouble(stepInput.getText());
            currentUser = SessionManager.getCurrentUser();
            if (!(currentUser instanceof Member currentMember)) {
                throw new IllegalArgumentException("[Error]: Only members can enable auto bidding.");
            }

            AutoBid config = new AutoBid(auction, currentMember, maxBid, increment);
            AuctionManager.getInstance().processAutoBids(auction, config);

            bidPlacedResultLabel.setTextFill(GREEN);
            bidPlacedResultLabel.setText("Auto Bid enabled successfully!");
        }  catch (CustomisedException e) {
            bidPlacedResultLabel.setTextFill(RED);
            bidPlacedResultLabel.setText(e.getMessage());
        } catch (NumberFormatException e) {
            bidPlacedResultLabel.setTextFill(RED);
            bidPlacedResultLabel.setText("Please enter a valid number.");
        }
    }
}
