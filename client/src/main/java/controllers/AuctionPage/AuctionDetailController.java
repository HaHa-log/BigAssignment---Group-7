package controllers.AuctionPage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.dto.bid.AutoBidRequest;
import javafx.application.Platform;
import javafx.scene.control.Button;
import models.*;
import models.Exceptions.CustomisedException;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import services.AuctionApiService;
import services.ItemApiService;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class AuctionDetailController {
    @FXML
    private Label statusLabel;
    @FXML
    private TextField bidAmountInput;
    @FXML
    private Label itemNameLabel;
    @FXML
    private Label startingPriceLabel;
    @FXML
    private Label currentPriceLabel;
    @FXML
    private Label ownerNameLabel, itemDescriptionLabel, auctionStatusLabel, durationLabel;
    @FXML
    private ImageView imageContainer;
    @FXML
    private LineChart<String, Number> bidHistoryChart;
    @FXML
    private VBox confirmPane, cancelPane;
    @FXML
    private TextField maxBidInput;
    @FXML
    private TextField stepInput;
    @FXML
    private Button normalBidButton, autoBidButton;
    @FXML
    private Button confirmButton, cancelButton;
    @FXML
    private Label confirmMessage1, confirmMessage2, cancelMessage;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final AuctionApiService auctionApiService = new AuctionApiService();
    private final ItemApiService itemApiService = new ItemApiService();
    private WebSocket webSocket;
    private volatile boolean wsConnecting = false;
    private static final HttpClient WS_CLIENT = HttpClient.newHttpClient();

    private User currentUser = SessionManager.getCurrentUser();
    private Auction auction;

    @FXML
    private void checkBidValidity() {
        String bidAmountString = bidAmountInput.getText();

        if (bidAmountString == null || bidAmountString.trim().isEmpty()) {
            statusLabel.setText("Please enter an amount.");
            return;
        }

        try {
            currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalArgumentException("[Error]: Session expired! Please log in again.");
            }
            double bidAmount = Double.parseDouble(bidAmountString);
            auction = auctionApiService.placeBid(auction.getId(), currentUser.getId(), bidAmount);

            statusLabel.setTextFill(GREEN);
            statusLabel.setText("Bid placed successfully.");
            currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            statusLabel.setText(message);
        } catch (CustomisedException e) {
            String message = e.getMessage();
            statusLabel.setText(message);
        } catch (Exception e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText(e.getMessage());
        } finally {
            bidAmountInput.clear();
            refreshAuction();
        }
    }

    public void setAuctionData(Auction initialAuction) {
        this.auction = initialAuction;

        this.itemNameLabel.setText(initialAuction.getItem().getName());
        this.startingPriceLabel.setText("Starting price: $" + initialAuction.getStartingPrice());
        this.currentPriceLabel.setText("Current price: $" + initialAuction.getCurrentPrice());

        setItemImage();
        getTableData(initialAuction);

        updateBidChart();

        CompletableFuture.supplyAsync(() -> {
            try {
                return auctionApiService.getById(initialAuction.getId());
            } catch (Exception e) {
                System.err.println("[ERROR] Lấy chi tiết lịch sử đấu giá thất bại: " + e.getMessage());
                return null;
            }
        }).thenAccept(detailedAuction -> {
            if (detailedAuction != null) {
                Platform.runLater(() -> {
                    this.auction = detailedAuction;
                    this.currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());

                    updateBidChart();
                    configureStatusAndPanes();
                });
            }
        });

        connectWebSocket();
    }

    private void configureStatusAndPanes() {
        cancelPane.setVisible(false);
        cancelPane.setManaged(false);

        confirmPane.setVisible(false);
        confirmPane.setManaged(false);

        boolean isRunning = auction.getStatus() != null && auction.getStatus() == Auction.AuctionStatus.RUNNING;
        if (!isRunning) {
            normalBidButton.setDisable(true);
            autoBidButton.setDisable(true);
            statusLabel.setText("Making bid is currently unavailable since auction status is not RUNNING.");
        }

        if (isRunning) {
            normalBidButton.setDisable(false);
            autoBidButton.setDisable(false);
            if (statusLabel.getText().startsWith("Making bid is currently unavailable")) {
                statusLabel.setText("");
            }
        }

        if (currentUser != null) {
            if (currentUser.isOwner(auction)) {
                if (auction.getStatus() == Auction.AuctionStatus.OPEN) {
                    setupCancelPane(false);
                } else if (auction.getStatus() == Auction.AuctionStatus.CANCELED) {
                    setupCancelPane(true);
                }
            }

            if (currentUser.isWinner(auction)) {
                if (auction.getStatus() == Auction.AuctionStatus.FINISHED) {
                    setupConfirmPane(false);
                } else if (auction.getStatus() == Auction.AuctionStatus.PAID) {
                    setupConfirmPane(true);
                }
            }
        }
    }


    private void setupConfirmPane(boolean alreadyConfirmed) {
        confirmPane.setVisible(true);
        confirmPane.setManaged(true);

        if (alreadyConfirmed) {
            confirmButton.setDisable(true);
            confirmMessage1.setText("Your transaction has been confirmed");
            confirmMessage2.setText("");
            confirmButton.setText("✓ Confirmed");
        } else {
            confirmButton.setDisable(false);
            confirmButton.setText("CONFIRM");}
    }

    private void setupCancelPane(boolean alreadyCanceled) {
        cancelPane.setVisible(true);
        cancelPane.setManaged(true);

        if (alreadyCanceled) {
            cancelButton.setDisable(true);
            cancelMessage.setText("Your auction has been canceled");
            cancelButton.setText("✓ Canceled");
        } else {
            cancelButton.setDisable(false);
            cancelButton.setText("CANCEL");}
    }

    public void getTableData(Auction auction) {
        ownerNameLabel.setText(auction.getOwner().getFullName());
        itemDescriptionLabel.setText(auction.getItem().getDescription());
        auctionStatusLabel.setText(auction.getStatus().toString());

        String startStr = auction.getStartingTime().format(dateTimeFormatter);
        String endStr = auction.getEndingTime().format(dateTimeFormatter);

        durationLabel.setText(startStr + " - " + endStr);
    }
    private void setItemImage() {
        Item item = auction.getItem();
        String filePath = item.getImagePath();

        if (filePath == null || filePath.isBlank() || "null".equalsIgnoreCase(filePath)) {
            imageContainer.setImage(null);
            return;
        }

        String imageUrl = itemApiService.getItemImageUrl(filePath);
        imageContainer.setImage(new Image(imageUrl, true));
    }

    private void updateBidChart() {
        if (auction == null) return;

        List<Bid> bids = auction.getBids();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Price History ($)");

        if (bids != null) {
            for (Bid bid : bids) {
                String time = (bid.getBidTime() != null)
                        ? bid.getBidTime().format(dateTimeFormatter)
                        : "N/A";
                series.getData().add(new XYChart.Data<>(time, bid.getBidPrice().getPrice()));
            }
        }

        bidHistoryChart.setAnimated(false);

        javafx.application.Platform.runLater(() -> {
            bidHistoryChart.getData().clear();
            bidHistoryChart.getData().add(series);
        });
    }

    private void connectWebSocket() {
        if (auction == null) return;
        if (webSocket != null && !webSocket.isInputClosed() && !webSocket.isOutputClosed()) { return; }
        if (wsConnecting) { return; }

        wsConnecting = true;
        int auctionId = auction.getId();

        WS_CLIENT.newWebSocketBuilder()
                .buildAsync(auctionApiService.bidWebSocketUri(auctionId),
                        new WebSocket.Listener() {

                            @Override
                            public void onOpen(WebSocket ws) {
                                System.out.println("[CLIENT WS]: Connected to auction " + auctionId);
                                ws.request(1);
                            }

                            @Override
                            public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                                // Call the unified refresh method
                                Platform.runLater(() -> refreshAuction());
                                ws.request(1);
                                return CompletableFuture.completedFuture(null);
                            }

                            @Override
                            public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
                                System.out.println("[CLIENT WS]: Closed " + statusCode + " " + reason);
                                webSocket = null;
                                wsConnecting = false;
                                return CompletableFuture.completedFuture(null);
                            }

                            @Override
                            public void onError(WebSocket ws, Throwable error) {
                                System.out.println("[CLIENT WS ERROR]");
                                error.printStackTrace();
                                webSocket = null;
                                wsConnecting = false;
                            }
                        })
                .thenAccept(ws -> {
                    webSocket = ws;
                    wsConnecting = false;
                    System.out.println("[CLIENT WS]: saved");
                })
                .exceptionally(e -> {
                    wsConnecting = false;
                    e.printStackTrace();
                    return null;
                });
    }

    public void closeWebSocket() {
        WebSocket ws = webSocket;
        webSocket = null;
        wsConnecting = false;

        if (ws != null && !ws.isInputClosed() && !ws.isOutputClosed()) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "leaving page");
        }
    }

    @FXML
    private void confirm() {
        try {
            currentUser = SessionManager.getCurrentUser();
            if (!(currentUser instanceof User member)) {
                throw new IllegalArgumentException("[Error]: Only members can confirm receipt.");
            }
            auction = auctionApiService.confirmReceipt(auction.getId(), member.getId());
            auctionStatusLabel.setText(auction.getStatus().toString());

            javafx.scene.control.Button confirmBtn = (javafx.scene.control.Button) confirmPane.getChildren().get(2);
            confirmBtn.setDisable(true);
            confirmBtn.setText("✓ Confirmed");

            statusLabel.setTextFill(GREEN);
            statusLabel.setText("Receipt confirmed. Transaction completed.");
        } catch (Exception e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        try {
            auction = auctionApiService.cancel(auction.getId());
            auctionStatusLabel.setText(auction.getStatus().toString());

            javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) cancelPane.getChildren().get(1);
            cancelBtn.setDisable(true);
            cancelBtn.setText("✓ Cancelled");

            statusLabel.setTextFill(GREEN);
            statusLabel.setText("Auction cancelled succesfully!");
        } catch (Exception e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void checkAutoBidValidity() {
        if (maxBidInput.getText().isEmpty() || stepInput.getText().isEmpty()) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("Please enter Max Bid and Increment.");
            return;
        }
        try {
            double maxBid = Double.parseDouble(maxBidInput.getText());
            double increment = Double.parseDouble(stepInput.getText());
            currentUser = SessionManager.getCurrentUser();
            if (!(currentUser instanceof User currentMember)) {
                throw new IllegalArgumentException("[Error]: Only members can enable auto bidding.");
            }

            AutoBidRequest request = new AutoBidRequest(
                    currentMember.getId(),
                    maxBid,
                    increment
            );

            auctionApiService.enableAutoBid(auction.getId(), request);

            statusLabel.setTextFill(GREEN);
            statusLabel.setText("Auto Bid enabled successfully!");

            maxBidInput.clear();
            stepInput.clear();
        } catch (CustomisedException e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText(e.getMessage());
        } catch (NumberFormatException e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("Please enter a valid number.");
        } catch (Exception e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText(e.getMessage());
        }
    }

    private void refreshAuction() {
        CompletableFuture.delayedExecutor(500, java.util.concurrent.TimeUnit.MILLISECONDS)
                .execute(() -> {
                    try {
                        Auction updatedAuction = auctionApiService.getById(auction.getId());
                        if (updatedAuction != null) {
                            Platform.runLater(() -> {
                                System.out.println("[CLIENT] UI Syncing with: " + updatedAuction.getStatus());
                                this.auction = updatedAuction;
                                currentPriceLabel.setText("Current price: $" + auction.getCurrentPrice());
                                auctionStatusLabel.setText(auction.getStatus().toString());
                                configureStatusAndPanes();
                                updateBidChart();
                                getTableData(auction);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
