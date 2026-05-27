package controllers.MainPage.ProfilePage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group7.dto.transaction.TransactionResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import exceptions.ApiException;
import models.Auction;
import services.UserApiService;
import services.AuctionApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class FinanceController extends BaseController {

    @FXML private VBox depositBox, withdrawBox;
    @FXML private TextField depositField, withdrawField;
    @FXML private Label balanceLabel, frozenBalanceLabel, totalBalanceLabel, depositStatus, withdrawStatus;
    @FXML private VBox frozenDetailsBox;
    @FXML private TableView<TransactionResponse> frozenTable;
    @FXML private TableColumn<TransactionResponse, String> colFrozenItem;
    @FXML private TableColumn<TransactionResponse, String> colFrozenAmount;
    @FXML private ProgressIndicator frozenLoadingIndicator;

    private final UserApiService userApiService = new UserApiService();

    @Override
    protected void initData() {
        refreshFinance();
    }

    @FXML
    private void handleDeposit() {
        depositBox.setVisible(true);
        depositBox.setManaged(true);
        withdrawBox.setVisible(false);
        withdrawBox.setManaged(false);
    }

    @FXML
    private void handleWithdraw() {
        withdrawBox.setVisible(true);
        withdrawBox.setManaged(true);
        depositBox.setVisible(false);
        depositBox.setManaged(false);
    }

    @FXML
    private void handleSaveDeposit() {
        try {
            double amount = Double.parseDouble(depositField.getText());
            user = userApiService.deposit(user.getId(), amount);  // gọi server
            depositStatus.getStyleClass().setAll("success");
            depositStatus.setText("Deposited " + amount + " successfully!");
            refreshFinance();
        } catch (ApiException e) {
            depositStatus.getStyleClass().setAll("error");
            depositStatus.setText(e.getMessage());
        } catch (Exception e) {
            depositStatus.getStyleClass().setAll("error");
            depositStatus.setText("Invalid amount.");
        }
        depositField.clear();
    }

    @FXML
    private void handleSaveWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawField.getText());
            user = userApiService.withdraw(user.getId(), amount);  // gọi server
            withdrawStatus.getStyleClass().setAll("success");
            withdrawStatus.setText("Withdrawn " + amount + " successfully!");
            refreshFinance();
        } catch (ApiException e) {
            withdrawStatus.getStyleClass().setAll("error");
            withdrawStatus.setText(e.getMessage());
        } catch (Exception e) {
            withdrawStatus.getStyleClass().setAll("error");
            withdrawStatus.setText("Invalid or exceeds balance.");
        }
        withdrawField.clear();
    }

    private void refreshFinance() {
        try {
            user = userApiService.getById(user.getId());  // fetch lại từ server
        } catch (Exception e) {
            e.printStackTrace();
        }
        balanceLabel.setText(String.format("%.2f", user.getBalance()));
        frozenBalanceLabel.setText(String.format("%.2f", user.getFrozenBalance()));
        totalBalanceLabel.setText(String.format("%.2f", user.getBalance() + user.getFrozenBalance()));
    }

    @FXML
    private void handleShowFrozenDetails() {
        if (frozenDetailsBox.isVisible()) {
            frozenDetailsBox.setVisible(false);
            frozenDetailsBox.setManaged(false);
            return;
        }

        frozenLoadingIndicator.setVisible(true);
        frozenLoadingIndicator.setManaged(true);

        colFrozenItem.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().itemName()));
        colFrozenAmount.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("$%.2f", c.getValue().finalAmount())));

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

                HttpRequest txReq = HttpRequest.newBuilder()
                        .uri(URI.create(config.ApiConfig.baseUrl()
                                + "/api/transactions/user/" + user.getId()))
                        .GET().build();
                List<TransactionResponse> allTx = mapper.readValue(
                        client.send(txReq, HttpResponse.BodyHandlers.ofString()).body(),
                        new com.fasterxml.jackson.core.type.TypeReference<>() {});

                HttpRequest histReq = HttpRequest.newBuilder()
                        .uri(URI.create(config.ApiConfig.baseUrl()
                                + "/api/users/" + user.getId() + "/history"))
                        .GET().build();
                List<com.group7.dto.user.HistoryEntryResponse> history = mapper.readValue(
                        client.send(histReq, HttpResponse.BodyHandlers.ofString()).body(),
                        new com.fasterxml.jackson.core.type.TypeReference<>() {});

                List<TransactionResponse> pending = allTx.stream()
                        .filter(t -> "PENDING".equals(t.status()))
                        .toList();

                AuctionApiService auctionApi = new AuctionApiService();
                List<TransactionResponse> leading = history.stream()
                        .filter(h -> "LEADING".equals(h.userState()))
                        .map(h -> {
                            try {
                                Auction a = auctionApi.getById(h.auctionId());
                                return new TransactionResponse(
                                        0, h.auctionId(), h.itemName(),
                                        user.getId(), user.getFullName(),
                                        0, "",
                                        a.getCurrentPrice(),
                                        "LEADING",
                                        null, null, null);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(t -> t != null)
                        .toList();

                java.util.Set<Integer> pendingIds = pending.stream()
                        .map(TransactionResponse::auctionId)
                        .collect(java.util.stream.Collectors.toSet());

                List<TransactionResponse> filteredLeading = leading.stream()
                        .filter(t -> !pendingIds.contains(t.auctionId()))
                        .toList();

                List<TransactionResponse> combined = new java.util.ArrayList<>();
                combined.addAll(pending);
                combined.addAll(filteredLeading);

                Platform.runLater(() -> {
                    frozenLoadingIndicator.setVisible(false);
                    frozenLoadingIndicator.setManaged(false);
                    frozenTable.setItems(
                            javafx.collections.FXCollections.observableArrayList(combined));
                    frozenDetailsBox.setVisible(true);
                    frozenDetailsBox.setManaged(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    frozenLoadingIndicator.setVisible(false);
                    frozenLoadingIndicator.setManaged(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleCloseFrozenDetails() {
        
        frozenDetailsBox.setVisible(false);
        frozenDetailsBox.setManaged(false);
    }
}