package controllers.MainPage.ProfilePage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group7.dto.user.HistoryEntryResponse;
import com.group7.dto.transaction.TransactionResponse;
import models.Auction;
import models.AuctionManager;
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryController extends BaseController {

    @FXML private ComboBox<String> filterBox;
    @FXML private TableView<AuctionHistoryEntry> historyTable;
    @FXML private TableView<TransactionResponse> transactionTable; // ← đổi từ ListView
    @FXML private ProgressIndicator loadingIndicator;

    @FXML private TableColumn<AuctionHistoryEntry, Integer> colAuction;
    @FXML private TableColumn<AuctionHistoryEntry, String>  colItem;
    @FXML private TableColumn<AuctionHistoryEntry, String>  colStatus;
    @FXML private TableColumn<AuctionHistoryEntry, String>  colState;
    @FXML private TableColumn<AuctionHistoryEntry, Void>    colAction;

    @FXML private TableColumn<TransactionResponse, Integer> colTxId;
    @FXML private TableColumn<TransactionResponse, String>  colTxItem;
    @FXML private TableColumn<TransactionResponse, String>  colTxAmount;
    @FXML private TableColumn<TransactionResponse, String>  colTxRole;
    @FXML private TableColumn<TransactionResponse, String>  colTxStatus;
    @FXML private TableColumn<TransactionResponse, String>  colTxDate;

    private final ObservableList<AuctionHistoryEntry> masterData       = FXCollections.observableArrayList();
    private final ObservableList<TransactionResponse> transactionData  = FXCollections.observableArrayList();

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTransactionColumns(); // ← thêm
        setupFilter();
        historyTable.setItems(masterData);
        transactionTable.setItems(transactionData); // ← thêm
        loadHistory();
    }

    @Override
    protected void initData() {
        loadHistory();
    }

    private void setupTableColumns() {
        colAuction.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().auctionId()));
        colItem.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().itemName()));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().auctionStatus()));
        colState.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().userState()));

        colState.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("state-win","state-lose","state-owner","state-default");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                switch (item) {
                    case "WON","LEADING"  -> getStyleClass().add("state-win");
                    case "LOST","OUTBID"  -> getStyleClass().add("state-lose");
                    case "MY AUCTION"     -> getStyleClass().add("state-owner");
                    default               -> getStyleClass().add("state-default");
                }
            }
        });

        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button confirmButton = new Button("Confirm");
            {
                confirmButton.getStyleClass().add("confirm-button");
                confirmButton.setOnAction(event -> {
                    AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());
                    try {
                        Auction targetAuction = AuctionManager.getInstance().getAllSessions().stream()
                                .filter(a -> a.getId() == entry.auctionId())
                                .findFirst().orElse(null);
                        if (targetAuction != null) {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                                    getClass().getResource("/AuctionPageFXML/AuctionDetail.fxml"));
                            javafx.scene.Parent detailRoot = loader.load();
                            AuctionDetailController detailController = loader.getController();
                            detailController.setAuctionData(targetAuction);
                            SceneManager.switchContent(detailRoot);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                AuctionHistoryEntry entry = getTableView().getItems().get(getIndex());
                Auction current = AuctionManager.getInstance().getAllSessions().stream()
                        .filter(a -> a.getId() == entry.auctionId())
                        .findFirst().orElse(null);
                boolean showConfirm = "WON".equals(entry.userState())
                        && current != null
                        && current.getRawStatus() != Auction.AuctionStatus.PAID;
                setGraphic(showConfirm ? confirmButton : null);
            }
        });
    }

    private void setupTransactionColumns() {
        colTxId.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().transactionId()));

        colTxItem.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().itemName()));

        colTxAmount.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.format("%,.0f VNĐ", c.getValue().finalAmount())));

        colTxRole.setCellValueFactory(c -> {
            TransactionResponse tx = c.getValue();
            String role = tx.buyerId() == user.getId() ? "BUYER" : "SELLER";
            return new SimpleStringProperty(role);
        });

        colTxStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().status()));
        colTxStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll(
                        "state-win", "state-lose", "state-default");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                switch (item) {
                    case "COMPLETED" -> getStyleClass().add("state-win");
                    case "REFUNDED"  -> getStyleClass().add("state-lose");
                    default          -> getStyleClass().add("state-default"); // PENDING
                }
            }
        });

        colTxDate.setCellValueFactory(c -> {
            TransactionResponse tx = c.getValue();
            String date = tx.completedAt() != null
                    ? tx.completedAt().format(DATE_FMT)
                    : tx.paidAt() != null
                      ? tx.paidAt().format(DATE_FMT)
                      : "-";
            return new SimpleStringProperty(date);
        });
    }

    private void loadHistory() {
        if (user == null) return;
        loadingIndicator.setManaged(true);
        loadingIndicator.setVisible(true);

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper()
                        .registerModule(new JavaTimeModule()); // ← cần cho LocalDateTime

                // 1. Load auction history
                HttpRequest histReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users/"
                                + user.getId() + "/history"))
                        .GET().build();
                List<HistoryEntryResponse> histResponse = mapper.readValue(
                        client.send(histReq, HttpResponse.BodyHandlers.ofString()).body(),
                        new TypeReference<>() {});

                List<AuctionHistoryEntry> historyData = histResponse.stream()
                        .map(h -> new AuctionHistoryEntry(
                                h.auctionId(), h.itemName(),
                                h.auctionStatus(), h.userState()))
                        .toList();

                //  Load transactions
                HttpRequest txReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/transactions/user/"
                                + user.getId()))
                        .GET().build();
                String txBody = client.send(txReq, HttpResponse.BodyHandlers.ofString()).body();
                System.out.println("[DEBUG] Transaction response: " + txBody);
                List<TransactionResponse> txResponse = mapper.readValue(
                        client.send(txReq, HttpResponse.BodyHandlers.ofString()).body(),
                        new TypeReference<>() {});

                Platform.runLater(() -> {
                    masterData.setAll(historyData);
                    transactionData.setAll(txResponse); // ← thêm
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setManaged(false);
                });

            } catch (Exception e) {
                System.err.println("Failed to load history: " + e.getMessage());
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    loadingIndicator.setManaged(false);
                });
            }
        }).start();
    }

    private void setupFilter() {
        filterBox.getItems().addAll("All", "Won", "Lost", "My Auctions");
        filterBox.setValue("All");
        filterBox.setOnAction(event -> applyFilter());
    }

    private void applyFilter() {
        String selected = filterBox.getValue();
        if ("All".equals(selected)) { historyTable.setItems(masterData); return; }
        ObservableList<AuctionHistoryEntry> filtered = masterData.stream()
                .filter(entry -> switch (selected) {
                    case "Won"        -> entry.userState().contains("WON");
                    case "Lost"       -> entry.userState().contains("LOST");
                    case "My Auctions"-> entry.userState().contains("MY AUCTION");
                    default           -> true;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        historyTable.setItems(filtered);
    }
}