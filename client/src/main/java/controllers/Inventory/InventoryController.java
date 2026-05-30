package controllers.Inventory;

import models.Item;
import models.User;
import models.Auction;
import models.SessionManager;
import services.ItemApiService;
import services.AuctionApiService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InventoryController {
    @FXML private FlowPane itemList;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ProgressIndicator loadingSpinner;

    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    private final User user = SessionManager.getCurrentUser();
    private final ItemApiService itemApiService = new ItemApiService();
    private final AuctionApiService auctionApiService = new AuctionApiService();

    private final Map<Integer, ItemCardController> cardControllersMap = new HashMap<>();

    private int currentPage = 0;
    private final int PAGE_SIZE = 20;

    @FXML
    private void initialize() {
        statusFilter.getItems().add("ALL");
        for (Item.Status status : Item.Status.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.setValue("ALL");

        statusFilter.setOnAction(e -> {
            currentPage = 0;
            populateList();
        });

        btnPrev.setOnAction(e -> handlePrevPage());
        btnNext.setOnAction(e -> handleNextPage());

        javafx.application.Platform.runLater(this::populateList);
    }

    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            populateList();
        }
    }

    private void handleNextPage() {
        currentPage++;
        populateList();
    }

    public void populateList() {
        String selectedStatus = statusFilter.getValue();
        itemList.getChildren().clear();
        cardControllersMap.clear();
        itemList.setVisible(false);
        loadingSpinner.setVisible(true);

        btnPrev.setDisable(true);
        btnNext.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                if (user == null) return;

                List<Item> items = itemApiService.fetchInventory(user.getId(), currentPage, PAGE_SIZE);

                List<Item> filteredItems = items.stream()
                        .filter(item -> selectedStatus.equals("ALL") || item.getStatus().name().equals(selectedStatus))
                        .toList();

                javafx.application.Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    itemList.setVisible(true);

                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(items.size() < PAGE_SIZE || filteredItems.isEmpty());

                    for (Item item : filteredItems) {
                        double displayPrice = item.getCurrentAuctionPrice() != null
                                ? item.getCurrentAuctionPrice()
                                : item.getStartingPrice();

                        try {
                            FXMLLoader loader = new FXMLLoader(
                                    getClass().getResource("/InventoryFXML/ItemCard.fxml")
                            );
                            HBox card = loader.load();
                            ItemCardController controller = loader.getController();
                            controller.setItemData(item, displayPrice);

                            cardControllersMap.put(item.getId(), controller);

                            itemList.getChildren().add(card);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    loadingSpinner.setVisible(false);
                    itemList.setVisible(true);
                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(items.size() < PAGE_SIZE);

                    syncInAuctionPrices();
                });
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(true);
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void syncInAuctionPrices() {
        boolean hasInAuctionItem = cardControllersMap.values().stream()
                .anyMatch(c -> c.getItem().getStatus() != null && "IN_AUCTION".equals(c.getItem().getStatus().name()));

        if (!hasInAuctionItem) return;

        Thread syncThread = new Thread(() -> {
            try {
                List<Auction> activeAuctions = auctionApiService.getAll(0, 100, "RUNNING");

                javafx.application.Platform.runLater(() -> {
                    for (Auction auction : activeAuctions) {
                        if (auction.getItem() != null) {
                            int itemId = auction.getItem().getId();

                            if (cardControllersMap.containsKey(itemId)) {
                                ItemCardController targetCard = cardControllersMap.get(itemId);

                                targetCard.updateCurrentPrice(auction.getCurrentPrice());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("[ERROR] Không thể sync giá đấu giá cho Inventory: " + e.getMessage());
            }
        });
        syncThread.setDaemon(true);
        syncThread.start();
    }
}