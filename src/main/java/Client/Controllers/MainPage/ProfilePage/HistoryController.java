package Client.Controllers.MainPage.ProfilePage;

import Branch.AuctionManager;
import Branch.Common.AuctionHistoryEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class HistoryController extends BaseController {

    @FXML private ComboBox<String> filterBox;

    @FXML private TableView<AuctionHistoryEntry> historyTable;

    // Fixed typo: Integer instead of Interger
    @FXML private TableColumn<AuctionHistoryEntry, Integer> colAuction;
    @FXML private TableColumn<AuctionHistoryEntry, String> colItem;
    @FXML private TableColumn<AuctionHistoryEntry, String> colStatus;
    @FXML private TableColumn<AuctionHistoryEntry, String> colState;

    private final ObservableList<AuctionHistoryEntry> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilter();
    }

    @Override
    protected void initData() {
        loadHistory();
    }

    private void setupTableColumns() {
        // Modern way: Directly call the record's methods
        colAuction.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().auctionId()));

        colItem.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().itemName()));

        colStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().auctionStatus()));

        colState.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().userState()));

        colState.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "WON", "LEADING" -> setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                        case "LOST", "OUTBID" -> setStyle("-fx-text-fill: #dc2626;");
                        case "MY AUCTION" -> setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #475569;");
                    }
                }
            }
        });
    }

    private void setupFilter() {
        filterBox.getItems().addAll("All", "Won", "Lost", "My Auctions");
        filterBox.setValue("All");
        filterBox.setOnAction(event -> applyFilter());
    }

    private void loadHistory() {
        if (user == null) return;

        masterData.clear();
        masterData.addAll(user.getTableHistory(AuctionManager.getInstance().getAllSessions()));
        historyTable.setItems(masterData);
    }

    private void applyFilter() {
        String selected = filterBox.getValue();

        if ("All".equals(selected)) {
            historyTable.setItems(masterData);
            return;
        }

        ObservableList<AuctionHistoryEntry> filtered = masterData.stream()
                .filter(entry -> switch (selected) {

                    case "Won" -> entry.userState().contains("WON");
                    case "Lost" -> entry.userState().contains("LOST");
                    case "My Auctions" -> entry.userState().contains("MY AUCTION");
                    default -> true;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        historyTable.setItems(filtered);
    }
}