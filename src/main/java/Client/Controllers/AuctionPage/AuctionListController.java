package Client.Controllers.AuctionPage;

import Branch.Auction;
import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AuctionListController {
    @FXML
    private TilePane auctionTilePane;
    @FXML
    private ComboBox<String> statusFilter;

    AuctionManager auctionManager = AuctionManager.getInstance();

    @FXML
    private void initialize() {
        statusFilter.getItems().add("ALL");
        for (Auction.AuctionStatus status : Auction.AuctionStatus.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.setValue("ALL");

        statusFilter.setOnAction(e -> populateList());

        populateList();
    }

    public void populateList() {
        auctionTilePane.getChildren().clear();
        List<Auction> auctions = auctionManager.getAllSessions();

        String selectedStatus = statusFilter.getValue();

        for (Auction auction : auctions) {
            if (!selectedStatus.equals("ALL") &&
                    !auction.getStatus().name().equals(selectedStatus)) {
                continue;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionCard.fxml"));
                VBox card = loader.load();

                AuctionCardController cardController = loader.getController();
                cardController.setAuctionData(auction);

                auctionTilePane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}