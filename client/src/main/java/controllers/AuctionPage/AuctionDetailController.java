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

        double bidAmount;
        try {
            bidAmount = Double.parseDouble(bidAmountString);
        } catch (NumberFormatException e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("Please enter a valid number.");
            return;
        }

        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("[Error]: Session expired! Please log in again.");
            return;
        }

        normalBidButton.setDisable(true);
        statusLabel.setText("Processing...");
        bidAmountInput.clear();

        new Thread(() -> {
            try {
                Auction updated = auctionApiService.placeBid(auction.getId(), currentUser.getId(), bidAmount);

                Platform.runLater(() -> {
                    auction = updated;
                    double actualPrice = auction.getCurrentPrice();
                    currentPriceLabel.setText("Current price: $" + actualPrice);

                    if (actualPrice > bidAmount) {
                        statusLabel.setTextFill(RED);
                        statusLabel.setText("Bid placed but you were immediately outbid! Current price: $" + actualPrice);
                    } else {
                        statusLabel.setTextFill(GREEN);
                        statusLabel.setText("Bid placed successfully. You are the highest bidder!");
                    }
                    normalBidButton.setDisable(false);
                    updateBidChart();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setTextFill(RED);
                    statusLabel.setText(e.getMessage());
                    normalBidButton.setDisable(false);
                });
            }
        }).start();
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
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    var node = mapper.readTree(data.toString());
                                    double newPrice = node.get("currentPrice").asDouble();
                                    String status = node.has("status") ? node.get("status").asText() : null;

                                    Platform.runLater(() -> {
                                        currentPriceLabel.setText("Current price: $" + newPrice);
                                        if (status != null) {
                                            auctionStatusLabel.setText(status);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

        double maxBid;
        double increment;
        try {
            maxBid = Double.parseDouble(maxBidInput.getText());
            increment = Double.parseDouble(stepInput.getText());
        } catch (NumberFormatException e) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("Please enter a valid number.");
            return;
        }

        currentUser = SessionManager.getCurrentUser();
        if (!(currentUser instanceof User currentMember)) {
            statusLabel.setTextFill(RED);
            statusLabel.setText("[Error]: Only members can enable auto bidding.");
            return;
        }

        autoBidButton.setDisable(true);
        statusLabel.setText("Processing...");

        AutoBidRequest request = new AutoBidRequest(currentMember.getId(), maxBid, increment);

        new Thread(() -> {
            try {
                auctionApiService.enableAutoBid(auction.getId(), request);
                Platform.runLater(() -> {
                    statusLabel.setTextFill(GREEN);
                    statusLabel.setText("Auto Bid enabled successfully!");
                    autoBidButton.setDisable(false);
                    maxBidInput.clear();
                    stepInput.clear();
                });
            } catch (CustomisedException e) {
                Platform.runLater(() -> {
                    statusLabel.setTextFill(RED);
                    statusLabel.setText(e.getMessage());
                    autoBidButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setTextFill(RED);
                    statusLabel.setText(e.getMessage());
                    autoBidButton.setDisable(false);
                });
            }
        }).start();
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
