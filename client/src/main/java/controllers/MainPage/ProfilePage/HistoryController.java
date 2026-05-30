package controllers.MainPage.ProfilePage;

import com.group7.dto.user.HistoryEntryResponse;
import com.group7.dto.transaction.TransactionResponse;
import models.Auction;
import models.Common.AuctionHistoryEntry;
import controllers.AuctionPage.AuctionDetailController;
import controllers.SceneManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AuctionApiService;
import services.TransactionApiService;
import services.UserApiService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HistoryController extends BaseController {

    @FXML private ComboBox<String> filterBox;
    @FXML private TableView<AuctionHistoryEntry> historyTable;
    @FXML private TableView<TransactionResponse>  transactionTable;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML private TableColumn<AuctionHistoryEntry, Integer> colAuction;
    @FXML private TableColumn<AuctionHistoryEntry, String>  colItem, colStatus, colState;
    @FXML private TableColumn<AuctionHistoryEntry, Void>    colAction;

    @FXML private TableColumn<TransactionResponse, Integer> colTxId;
    @FXML private TableColumn<TransactionResponse, String>
            colTxItem, colTxAmount, colTxRole, colTxStatus, colTxDate;

    private final ObservableList<AuctionHistoryEntry> masterData      = FXCollections.observableArrayList();
    private final ObservableList<TransactionResponse>  transactionData = FXCollections.observableArrayList();

    private final AuctionApiService     auctionApiService     = new AuctionApiService();
    private final TransactionApiService transactionApiService = new TransactionApiService();
    private final UserApiService        userApiService        = new UserApiService();

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupAuctionColumns();
        setupTransactionColumns();
        setupFilter();
        historyTable.setItems(masterData);
        transactionTable.setItems(transactionData);
        loadHistory();
    }

    @Override
    protected void initData() { loadHistory(); }

    private void loadHistory() {
        if (user == null) return;
        setLoading(true);

        new Thread(() -> {
            try {
                List<AuctionHistoryEntry> history = userApiService
                        .getAuctionHistory(user.getId()).stream()
                        .map(h -> new AuctionHistoryEntry(
                                h.auctionId(), h.itemName(),
                                h.auctionStatus(), h.userState()))
                        .toList();

                List<TransactionResponse> transactions =
                        transactionApiService.getByUserId(user.getId());

                Platform.runLater(() -> {
                    masterData.setAll(history);
                    transactionData.setAll(transactions);
                    setLoading(false);
                });
            } catch (Exception e) {
                System.err.println("Failed to load history: " + e.getMessage());
                Platform.runLater(() -> setLoading(false));
            }
        }).start();
    }

    private void setLoading(boolean on) {
        loadingIndicator.setVisible(on);
        loadingIndicator.setManaged(on);
    }

    private void setupFilter() {
        filterBox.getItems().addAll("All", "Won", "Lost", "My Auctions");
        filterBox.setValue("All");
        filterBox.setOnAction(e -> applyFilter());
    }

    private void applyFilter() {
        String selected = filterBox.getValue();
        if ("All".equals(selected)) { historyTable.setItems(masterData); return; }

        ObservableList<AuctionHistoryEntry> filtered = masterData.stream()
                .filter(entry -> switch (selected) {
                    case "Won"         -> entry.userState().contains("WON");
                    case "Lost"        -> entry.userState().contains("LOST");
                    case "My Auctions" -> entry.userState().contains("MY AUCTION");
                    default            -> true;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        historyTable.setItems(filtered);
    }

    private void setupAuctionColumns() {
        colAuction.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().auctionId()));
        colItem   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().itemName()));
        colStatus .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().auctionStatus()));
        colState  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().userState()));

        colState.setCellFactory(col -> stateCell(item -> switch (item) {
            case "WON", "LEADING" -> "state-win";
            case "LOST", "OUTBID" -> "state-lose";
            case "MY AUCTION"     -> "state-owner";
            default               -> "state-default";
        }));

        colAction.setCellFactory(col -> new ActionButtonCell());
    }


    private void setupTransactionColumns() {
        colTxId    .setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().transactionId()));
        colTxItem  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().itemName()));
        colTxAmount.setCellValueFactory(c -> new SimpleStringProperty(String.format("%,.0f $", c.getValue().finalAmount())));
        colTxRole  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().buyerId() == user.getId() ? "BUYER" : "SELLER"));
        colTxStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        colTxDate  .setCellValueFactory(c -> {
            var tx = c.getValue();
            String date = tx.completedAt() != null ? tx.completedAt().format(DATE_FMT)
                    : tx.paidAt()!= null ? tx.paidAt().format(DATE_FMT) : "-";
            return new SimpleStringProperty(date);
        });

        colTxStatus.setCellFactory(col -> stateCell(item -> switch (item) {
            case "COMPLETED" -> "state-win";
            case "REFUNDED"  -> "state-lose";
            case "PENDING"   -> "state-owner";
            default          -> "state-default";
        }));
    }

    private static <T> TableCell<T, String> stateCell(Function<String, String> classifier) {
        return new TableCell<>() {
            private static final List<String> STATE_CLASSES =
                    List.of("state-win", "state-lose", "state-owner", "state-default");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll(STATE_CLASSES);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                getStyleClass().add(classifier.apply(item));
            }
        };
    }

    private void navigateToDetail(Auction auction) {
        try {
            var loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/AuctionPageFXML/AuctionDetail.fxml"));
            javafx.scene.Parent root = loader.load();
            AuctionDetailController ctrl = loader.getController();
            ctrl.setAuctionData(auction);
            SceneManager.switchContent(root);
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    private class ActionButtonCell extends TableCell<AuctionHistoryEntry, Void> {

        private final Button btn = new Button();

        ActionButtonCell() {
            btn.setOnAction(e -> {
                AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());
                new Thread(() -> {
                    try {
                        Auction auction = auctionApiService.getById(entry.auctionId());
                        Platform.runLater(() -> navigateToDetail(auction));
                    } catch (Exception ex) { ex.printStackTrace(); }
                }).start();
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) { setGraphic(null); return; }

            AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());
            String state  = entry.userState();
            String status = entry.auctionStatus();

            record Cfg(String text, String style, boolean enabled) {}

            Cfg cfg = null;
            if      ("WON".equals(state)        && "FINISHED".equals(status)) cfg = new Cfg("Confirm Receipt",    "btn-confirm",  true);
            else if ("WON".equals(state)        && "PAID".equals(status))     cfg = new Cfg("✓ Confirmed",        "btn-confirmed", false);
            else if ("MY AUCTION".equals(state) && "OPEN".equals(status))     cfg = new Cfg("Cancel",              "btn-cancel",   true);
            else if ("MY AUCTION".equals(state) && "CANCELED".equals(status)) cfg = new Cfg("Canceled",            "btn-canceled", false);
            else if ("MY AUCTION".equals(state) && "PAID".equals(status)) cfg = new Cfg("$ Received Payment", "btn-received", false);
            if (cfg == null) { setGraphic(null); return; }

            btn.setText(cfg.text());
            btn.setDisable(!cfg.enabled());
            btn.getStyleClass().setAll(cfg.style());
            setGraphic(btn);
        }
    }
}