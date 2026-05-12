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

//For Price Visualization
import model.impl.DaoFactory;
import model.BidsDAO;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML
    private LineChart<String, Number> bidHistoryChart;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");


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
        } catch (CustomisedException e) {
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
        updateBidChart();
    }

    public void getTableData(Auction auction) {
        ownerNameLabel.setText(auction.getOwner().getFullName());
        itemDescriptionLabel.setText(auction.getItem().getDescription());
        auctionStatusLabel.setText(auction.getStatus().toString());

        String startStr = auction.getStartingTime().format(dateFormatter);
        String endStr = auction.getEndingTime().format(dateFormatter);

        durationLabel.setText(startStr + " - " + endStr);
    }

    private void setItemImage() {
        Item item = auction.getItem();
        String filePath = item.getImagePath();

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
                    ? bid.getBidTime().format(timeFormatter)
                    : "N/A";
            series.getData().add(new XYChart.Data<>(time, bid.getBidPrice().getPrice()));
        }

        bidHistoryChart.getData().clear();
        bidHistoryChart.getData().add(series);

        bidHistoryChart.setAnimated(false);
    }

    public void setupAuction(Auction selectedAuction) {
        this.auction = selectedAuction;
        this.auction.addObserver((AuctionObserver) SessionManager.getCurrentUser());
    }
}
