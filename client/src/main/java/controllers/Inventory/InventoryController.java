package controllers.Inventory;

import models.Item;
import models.User;
import models.SessionManager;
import services.ItemApiService;
import services.AuctionApiService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryController {
    @FXML private FlowPane itemList;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ProgressIndicator loadingSpinner;

    private final User user = SessionManager.getCurrentUser();
    private final ItemApiService itemApiService = new ItemApiService();
    private final AuctionApiService auctionApiService = new AuctionApiService();

    @FXML
    private void initialize() {
        statusFilter.getItems().add("ALL");
        for (Item.Status status : Item.Status.values()) {
            statusFilter.getItems().add(status.name());
        }
        statusFilter.setValue("ALL");
        statusFilter.setOnAction(e -> populateList());

        javafx.application.Platform.runLater(this::populateList);
    }

    public void populateList() {
        String selectedStatus = statusFilter.getValue();
        itemList.getChildren().clear();
        itemList.setVisible(false);
        loadingSpinner.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                if (user == null) return;

                List<Item> items = itemApiService.fetchInventory(user.getId());
                List<models.Auction> activeAuctions = auctionApiService.getAll();

                Map<Integer, models.Auction> auctionMap = activeAuctions.stream()
                        .collect(Collectors.toMap(models.Auction::getItemId, a -> a, (a1, a2) -> a1));

                javafx.application.Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    itemList.setVisible(true);
                });

                for (Item item : items) {
                    if (!selectedStatus.equals("ALL") && !item.getStatus().name().equals(selectedStatus)) {
                        continue;
                    }

                    double dynamicPrice = item.getStartingPrice();
                    if (item.getStatus() == Item.Status.IN_AUCTION && auctionMap.containsKey(item.getId())) {
                        dynamicPrice = auctionMap.get(item.getId()).getCurrentPrice();
                    }

                    final double finalPrice = dynamicPrice;

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/InventoryFXML/ItemCard.fxml"));
                    HBox card = loader.load();
                    ItemCardController cardController = loader.getController();

                    javafx.application.Platform.runLater(() -> {
                        cardController.setItemData(item, finalPrice);
                        itemList.getChildren().add(card);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> loadingSpinner.setVisible(false));
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}