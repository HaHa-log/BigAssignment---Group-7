package controllers.MainPage.ProfilePage;

import com.group7.dto.transaction.TransactionResponse;
import com.group7.dto.user.HistoryEntryResponse;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import exceptions.ApiException;
import models.Auction;
import models.SessionManager;
import services.UserApiService;
import services.AuctionApiService;
import services.TransactionApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FinanceController extends BaseController {

    @FXML private VBox depositBox, withdrawBox, frozenDetailsBox;
    @FXML private TextField depositField, withdrawField;
    @FXML private Label balanceLabel, frozenBalanceLabel, totalBalanceLabel, depositStatus, withdrawStatus;
    @FXML private TableView<TransactionResponse> frozenTable;
    @FXML private TableColumn<TransactionResponse, String> colFrozenItem, colFrozenAmount;
    @FXML private ProgressIndicator frozenLoadingIndicator;

    private final UserApiService     userApiService     = new UserApiService();
    private final TransactionApiService transactionApiService = new TransactionApiService();
    private final AuctionApiService  auctionApiService  = new AuctionApiService();

    @Override
    protected void initData() {
        setupFrozenTableColumns();
        refreshFinance();
    }

    @FXML private void handleDeposit()  { toggleForms(depositBox,  withdrawBox); }
    @FXML private void handleWithdraw() { toggleForms(withdrawBox, depositBox);  }

    @FXML
    private void handleSaveDeposit() {
        executeTransaction(
                depositField, depositStatus,
                amount -> {
                    try {
                        user = userApiService.deposit(user.getId(), amount);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Deposited " + amount + " successfully!";
                }
        );
    }

    @FXML
    private void handleSaveWithdraw() {
        executeTransaction(
                withdrawField, withdrawStatus,
                amount -> {
                    try {
                        user = userApiService.withdraw(user.getId(), amount);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Withdrawn " + amount + " successfully!";
                }
        );
    }

    @FXML
    private void handleShowFrozenDetails() {
        if (frozenDetailsBox.isVisible()) {
            setFrozenVisible(false);
            return;
        }

        setFrozenLoading(true);

        new Thread(() -> {
            try {
                List<TransactionResponse> combined = buildFrozenList();
                Platform.runLater(() -> {
                    frozenTable.setItems(FXCollections.observableArrayList(combined));
                    setFrozenLoading(false);
                    setFrozenVisible(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> setFrozenLoading(false));
            }
        }).start();
    }

    @FXML
    private void handleCloseFrozenDetails() { setFrozenVisible(false); }

    private void refreshFinance() {
        try {
            user = userApiService.getById(user.getId());
            SessionManager.updateCurrentUser(user);
        } catch (Exception e) { e.printStackTrace(); }

        balanceLabel      .setText(String.format("%.2f", user.getBalance()));
        frozenBalanceLabel.setText(String.format("%.2f", user.getFrozenBalance()));
        totalBalanceLabel .setText(String.format("%.2f", user.getBalance() + user.getFrozenBalance()));
    }

    private void setupFrozenTableColumns() {
        colFrozenItem  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().itemName()));
        colFrozenAmount.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("$%.2f", c.getValue().finalAmount())));
    }

    private void toggleForms(VBox show, VBox hide) {
        show.setVisible(true);  show.setManaged(true);
        hide.setVisible(false); hide.setManaged(false);
    }

    private void executeTransaction(TextField field, Label statusLabel, java.util.function.Function<Double, String> action) {
        try {
            double amount = Double.parseDouble(field.getText());
            String msg = action.apply(amount);
            SessionManager.updateCurrentUser(user);
            setStatus(statusLabel, msg, true);
            refreshFinance();
        } catch (ApiException e) {
            setStatus(statusLabel, e.getMessage(), false);
        } catch (Exception e) {
            setStatus(statusLabel, "Invalid amount.", false);
        }
        field.clear();
    }

    private void setStatus(Label label, String message, boolean success) {
        label.getStyleClass().setAll(success ? "success" : "error");
        label.setText(message);
    }

    private void setFrozenVisible(boolean visible) {
        frozenDetailsBox.setVisible(visible);
        frozenDetailsBox.setManaged(visible);
    }

    private void setFrozenLoading(boolean loading) {
        frozenLoadingIndicator.setVisible(loading);
        frozenLoadingIndicator.setManaged(loading);
    }

    private List<TransactionResponse> buildFrozenList() throws Exception {
        List<TransactionResponse> allTx  = transactionApiService.getByUserId(user.getId());
        List<HistoryEntryResponse> history = userApiService.getAuctionHistory(user.getId());

        List<TransactionResponse> pending = allTx.stream()
                .filter(t -> "PENDING".equals(t.status()))
                .toList();

        Set<Integer> pendingAuctionIds = pending.stream()
                .map(TransactionResponse::auctionId)
                .collect(Collectors.toSet());

        List<TransactionResponse> leading = history.stream()
                .filter(h -> "LEADING".equals(h.userState())
                        && !pendingAuctionIds.contains(h.auctionId()))
                .map(h -> toLeadingTransaction(h))
                .filter(t -> t != null)
                .toList();

        List<TransactionResponse> combined = new ArrayList<>(pending);
        combined.addAll(leading);
        return combined;
    }

    private TransactionResponse toLeadingTransaction(HistoryEntryResponse h) {
        try {
            Auction a = auctionApiService.getById(h.auctionId());
            return new TransactionResponse(0, h.auctionId(), h.itemName(),
                    user.getId(), user.getFullName(), 0, "", a.getCurrentPrice(),
                    "LEADING", null, null, null);
        } catch (Exception e) {
            return null;
        }
    }
}