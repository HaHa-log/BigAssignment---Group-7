package controllers.Management;

import models.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import services.AuctionApiService;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionManagementController extends ManagementController<Auction> {
    @FXML
    private TableView<Auction> table;
    @FXML
    private TableColumn<Auction, Integer> auctionsId;
    @FXML
    private TableColumn<Auction, String> ownerName, itemName;
    @FXML
    private TableColumn<Auction, String> status;
    @FXML
    private TableColumn<Auction, Double> startingPrice, currentPrice;
    @FXML
    private TableColumn<Auction, LocalDateTime> startingTime, endingTime;
    @FXML
    private TableColumn<Auction, Void> actionColumn;

    private final AuctionManager auctionManager = AuctionManager.getInstance();
    private final AuctionApiService auctionApiService = new AuctionApiService();

    @Override
    protected void configureColumns() {
        auctionsId.setCellValueFactory(new PropertyValueFactory<>("id"));

        ownerName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOwner().getFullName()));
        itemName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getItem().getName()));

        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        startingPrice.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        startingTime.setCellValueFactory(new PropertyValueFactory<>("startingTime"));
        endingTime.setCellValueFactory(new PropertyValueFactory<>("endingTime"));

        setupActionColumn();
    }

    @Override
    protected List<Auction> fetchData() {
        try {
            return auctionApiService.getAllForManagement();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return auctionManager.getAllSessions();
        }
    }

    private void setupActionColumn() {
        Callback<TableColumn<Auction, Void>, TableCell<Auction, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Auction, Void> call(final TableColumn<Auction, Void> param) {
                return new TableCell<>() {
                    private final Button button = new Button("Cancel");

                    {
                        button.setOnAction(event -> {
                            Auction auction = getTableView().getItems().get(getIndex());
                            handleCancelAuction(auction);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Auction auction = getTableView().getItems().get(getIndex());

                            Auction.AuctionStatus currentStatus = auction.getStatus();

                            boolean shouldDisable = currentStatus == Auction.AuctionStatus.CANCELED ||
                                    currentStatus == Auction.AuctionStatus.FINISHED ||
                                    currentStatus == Auction.AuctionStatus.PAID;

                            button.setDisable(shouldDisable);
                            setGraphic(button);
                        }
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void handleCancelAuction(Auction auction) {
        try {
            auctionApiService.cancel(auction.getId());
            handleRefresh();
        } catch (Exception e) {
            User curentUser = SessionManager.getCurrentUser();

            if (curentUser == null || curentUser.isAdmin()) {
                System.err.println("Only admin can cancel aucitons.");
                return;
            }

            boolean success = auctionManager.cancelAuction(auction.getId());
            if (success) {
                handleRefresh();
            } else {
                System.err.println(e.getMessage());
            }
        }
    }
}
