package Client.Controllers.Management;

import Branch.Auction;
import Branch.AuctionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for the Management UI.
 * Handles loading auction data from the AuctionManager into the TableView.
 */
public class ManagementController {

    @FXML
    private TableView<Auction> auctionTable;

    @FXML
    private TableColumn<Auction, Integer> auctionsId, ownerId, itemId;
    @FXML
    private TableColumn<Auction, String> status;
    @FXML
    private TableColumn<Auction, Double> startingPrice, currentPrice;
    @FXML
    private TableColumn<Auction, LocalDateTime> startingTime, endingTime;
    @FXML

    private final AuctionManager auctionManager = AuctionManager.getInstance();

    @FXML
    public void initialize() {
        configureColumns();
        loadTableData();
    }

    /**
     * Links each TableColumn to the corresponding property in the Auction class.
     * Note: PropertyValueFactory looks for public getters (e.g., getAuctionsId()).
     */
    private void configureColumns() {
        auctionsId.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        ownerId.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        itemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        startingPrice.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        startingTime.setCellValueFactory(new PropertyValueFactory<>("startingTime"));
        endingTime.setCellValueFactory(new PropertyValueFactory<>("endingTime"));

        auctionTable.setPlaceholder(new javafx.scene.control.Label("No active auctions found."));
    }

    public void loadTableData() {
        List<Auction> activeList = auctionManager.getActiveSessions();

        if (activeList != null) {
            ObservableList<Auction> observableList = FXCollections.observableArrayList(activeList);
            auctionTable.setItems(observableList);
        }
    }

    @FXML
    private void handleRefresh() {
        loadTableData();
        System.out.println("Table Refreshed");
    }
}