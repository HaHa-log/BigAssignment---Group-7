package controllers.MainPage.ProfilePage;

import models.Auction;
import models.AuctionManager;
import models.Common.AuctionHistoryEntry;
import controllers.AuctionPage.AuctionDetailController;
import controllers.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class HistoryController extends BaseController {

    @FXML private ComboBox<String> filterBox;

    @FXML private TableView<AuctionHistoryEntry> historyTable;

    @FXML private ListView<String> transactionList;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML private TableColumn<AuctionHistoryEntry, Integer> colAuction;
    @FXML private TableColumn<AuctionHistoryEntry, String> colItem;
    @FXML private TableColumn<AuctionHistoryEntry, String> colStatus;
    @FXML private TableColumn<AuctionHistoryEntry, String> colState;
    @FXML private TableColumn<AuctionHistoryEntry, Void> colAction;

    private final ObservableList<AuctionHistoryEntry> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilter();
        setupTransactionList();

        historyTable.setItems(masterData);
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

        colAction.setCellFactory(column -> new TableCell<>() {

            private final Button confirmButton = new Button("Confirm");

            {
                confirmButton.getStyleClass().add("confirm-button");
                confirmButton.setOnAction(event -> {

                    AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());

                    /*try {
                        Auction targetAuction = AuctionManager.getInstance().getAllSessions().stream()
                                .filter(a -> a.getId() == entry.auctionId())
                                .findFirst()
                                .orElse(null);

                        if (targetAuction != null) {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionDetail.fxml"));
                            javafx.scene.Parent detailRoot = loader.load();

                            AuctionDetailController detailController = loader.getController();
                            detailController.setAuctionData(targetAuction);

                            SceneManager.switchContent(detailRoot);
                        } else {
                            System.err.println("[Error]: Cannot find the specified auction data to complete payment.");
                        }

                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.err.println("[System Error]: " + e.getMessage());
                        e.printStackTrace();
                    }*/
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());

                /*Auction currentAuction = AuctionManager.getInstance().getAllSessions().stream()
                        .filter(a -> a.getId() == entry.auctionId())
                        .findFirst()
                        .orElse(null);

                if (entry.userState() != null && entry.userState().contains("WON")
                        && currentAuction != null && currentAuction.getRawStatus() != Auction.AuctionStatus.PAID) {
                    setGraphic(confirmButton);
                } else {
                    setGraphic(null);
                }*/
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
                        "transaction-freeze",
                        "transaction-unfreeze",
                        "transaction-payment",
                        "transaction-default"
                );

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(item);

                if (item.contains("DEPOSIT")) {getStyleClass().add("transaction-deposit");

                } else if (item.contains("WITHDRAW")) {getStyleClass().add("transaction-withdraw");

                } else if (item.contains("UNFREEZE")) {getStyleClass().add("transaction-unfreeze");

                } else if (item.contains("FREEZE")) {getStyleClass().add("transaction-freeze");

                } else if (item.contains("PAYMENT")) {getStyleClass().add("transaction-payment");

                } else {getStyleClass().add("transaction-default");
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

        loadingIndicator.setManaged(true);
        loadingIndicator.setVisible(true);

        new Thread(() -> {

            /*ObservableList<AuctionHistoryEntry> data =
                    FXCollections.observableArrayList(
                            user.getTableHistory(
                                    AuctionManager.getInstance().getAllSessions()
                            )
                    );

            Platform.runLater(() -> {

                masterData.setAll(data);
                historyTable.setItems(masterData);
                transactionList.setItems(user.getTransactions());

                loadingIndicator.setVisible(false);
                loadingIndicator.setManaged(false);
            });*/

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

        historyTable.setItems(FXCollections.observableArrayList(filtered));
    }
}