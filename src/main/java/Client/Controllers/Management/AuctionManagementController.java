package Client.Controllers.Management;

import Branch.Auction;
import Branch.AuctionManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.List;

public class AuctionManagementController extends ManagementController<Auction> {
    @FXML
    private TableView<Auction> table;
    @FXML
    private TableColumn<Auction, Integer> auctionsId, ownerId, itemId;
    @FXML
    private TableColumn<Auction, String> status;
    @FXML
    private TableColumn<Auction, Double> startingPrice, currentPrice;
    @FXML
    private TableColumn<Auction, LocalDateTime> startingTime, endingTime;

    private final AuctionManager auctionManager = AuctionManager.getInstance();

    @Override
    protected void configureColumns() {
        auctionsId.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        ownerId.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        itemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        startingPrice.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        startingTime.setCellValueFactory(new PropertyValueFactory<>("startingTime"));
        endingTime.setCellValueFactory(new PropertyValueFactory<>("endingTime"));
    }

    @Override
    protected List<Auction> fetchData() {
        return auctionManager.getActiveSessions();
    }
}