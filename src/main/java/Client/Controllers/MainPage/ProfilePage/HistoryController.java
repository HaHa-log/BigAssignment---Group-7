package Client.Controllers.MainPage.ProfilePage;

import Branch.AuctionManager;
import Branch.Common.AuctionHistoryEntry;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ProgressIndicator;

import java.util.List;
import java.util.stream.Collectors;

public class HistoryController extends BaseController {

    @FXML private ComboBox<String> filterBox;

    @FXML private TableView<AuctionHistoryEntry> historyTable;

    @FXML private ListView<String> transactionList;

    @FXML
    private ProgressIndicator loadingIndicator;

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
        setupTransactionList();
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

                getStyleClass().removeAll(
                        "state-win",
                        "state-lose",
                        "state-owner",
                        "state-default"
                );

                if (empty || item == null) {setText(null);return;}

                setText(item);

                switch (item) {
                    case "WON", "LEADING" -> getStyleClass().add("state-win");

                    case "LOST", "OUTBID" -> getStyleClass().add("state-lose");

                    case "MY AUCTION" -> getStyleClass().add("state-owner");

                    default -> getStyleClass().add("state-default");
                }
            }
        });
    }

    private void setupTransactionList() {

        transactionList.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(
                        "transaction-deposit",
                        "transaction-withdraw",
                        "transaction-default"
                );

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item);

                if (item.contains("DEPOSIT")) {getStyleClass().add("transaction-deposit");

                } else if (item.contains("WITHDRAW")) {getStyleClass().add("transaction-withdraw");

                } else {getStyleClass().add("transaction-default");}
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

        loadingIndicator.setManaged(true);
        loadingIndicator.setVisible(true);

        new Thread(() -> {

            ObservableList<AuctionHistoryEntry> data =
                    FXCollections.observableArrayList(
                            user.getTableHistory(
                                    AuctionManager.getInstance().getAllSessions()
                            )
                    );

            transactionList.setItems(user.getTransactions());

            Platform.runLater(() -> {

                masterData.setAll(data);
                historyTable.setItems(masterData);
                transactionList.setItems(user.getTransactions());

                loadingIndicator.setVisible(false);
                loadingIndicator.setManaged(false);
            });

        }).start();
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