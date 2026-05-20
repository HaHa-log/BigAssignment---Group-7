package Client.Controllers.AuctionPage;

import Branch.Auction;
import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AuctionListController {
    @FXML
    private TilePane auctionTilePane;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ProgressIndicator loadingSpinner;

    AuctionManager auctionManager = AuctionManager.getInstance();

    @FXML
    private void initialize() {
        statusFilter.getItems().add("ALL");
        for (Auction.AuctionStatus status : Auction.AuctionStatus.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.setValue("ALL");

        statusFilter.setOnAction(e -> populateList());

        javafx.application.Platform.runLater(() -> {
            populateList();
        });
    }

    public void populateList() {
        String selectedStatus = statusFilter.getValue();
        auctionTilePane.getChildren().clear();
        auctionTilePane.setVisible(false);
        loadingSpinner.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                List<Auction> auctions = auctionManager.getAllSessions();

                // Hide spinner as soon as data is fetched
                javafx.application.Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    auctionTilePane.setVisible(true);
                });
                for (Auction auction : auctions) {
                    if (!selectedStatus.equals("ALL") && !auction.getStatus().name().equals(selectedStatus)) {
                        continue;
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionCard.fxml"));
                    VBox card = loader.load();
                    AuctionCardController cardController = loader.getController();
                    cardController.setAuctionData(auction);

                    javafx.application.Platform.runLater(() -> auctionTilePane.getChildren().add(card));
                }
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> loadingSpinner.setVisible(false));
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}