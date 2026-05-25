package controllers.Inventory;

import models.Item;
import models.User;
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
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryController {
    @FXML private FlowPane itemList;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ProgressIndicator loadingSpinner;

    // ĐỒNG BỘ UI: Thêm 2 nút điều hướng phân trang
    @FXML private Button btnPrev;
    @FXML private Button btnNext;

    private final User user = SessionManager.getCurrentUser();
    private final ItemApiService itemApiService = new ItemApiService();
    private final AuctionApiService auctionApiService = new AuctionApiService();

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
        itemList.setVisible(false);
        loadingSpinner.setVisible(true);

        // Khóa tạm thời nút bấm tránh spam click khi đang load dữ liệu
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

                    // Nút Previous sáng lên nếu không phải trang 0
                    btnPrev.setDisable(currentPage == 0);
                    // Nút Next tắt đi nếu số item trả về nhỏ hơn giới hạn PAGE_SIZE (hết dữ liệu)
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
                            itemList.getChildren().add(card);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    loadingSpinner.setVisible(false);
                    itemList.setVisible(true);
                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(items.size() < PAGE_SIZE);
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
}