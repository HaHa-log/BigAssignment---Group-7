package controllers.AuctionPage;

import models.Auction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import services.AuctionApiService;

import java.util.ArrayList;
import java.util.List;

public class AuctionListController {
    @FXML
    private TilePane auctionTilePane;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ProgressIndicator loadingSpinner;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;

    private final AuctionApiService auctionApiService = new AuctionApiService();

    private int currentPage = 0;
    private final int PAGE_SIZE = 10;

    // --- CƠ CHẾ CACHE ĐỂ CHẶN RELOAD KHI ĐỔI TAB ---
    private static List<Auction> cachedAuctions = null;
    private static int cachedPage = -1;
    private static String cachedStatus = "ALL";

    @FXML
    private void initialize() {
        statusFilter.getItems().add("ALL");
        for (Auction.AuctionStatus status : Auction.AuctionStatus.values()) {
            statusFilter.getItems().add(status.name());
        }

        // Khôi phục lại trạng thái filter cũ từ cache (nếu có)
        statusFilter.setValue(cachedStatus);

        statusFilter.setOnAction(e -> {
            currentPage = 0;
            cachedStatus = statusFilter.getValue();
            invalidateCacheAndReload(); // Đổi bộ lọc thì bắt buộc phải clear cache để load lại
        });

        btnPrev.setOnAction(e -> handlePrevPage());
        btnNext.setOnAction(e -> handleNextPage());

        // Kiểm tra xem đã có dữ liệu cache của trang này chưa
        if (cachedAuctions != null && cachedPage == currentPage) {
            // Có cache rồi: Hiển thị luôn lên màn hình, không gọi API nữa!
            renderAuctionUI(cachedAuctions);
        } else {
            // Chưa có cache: Tiến hành gọi lên Server
            Platform.runLater(this::populateList);
        }
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

    // Hàm chủ động xóa cache khi người dùng bấm nút Refresh (nếu bạn làm nút refresh sau này) hoặc đổi bộ lọc
    public void invalidateCacheAndReload() {
        cachedAuctions = null;
        populateList();
    }

    public void populateList() {
        auctionTilePane.getChildren().clear();
        auctionTilePane.setVisible(false);
        loadingSpinner.setVisible(true);

        btnPrev.setDisable(true);
        btnNext.setDisable(true);
        statusFilter.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                // Gọi API lấy dữ liệu từ Server về
                String selectedStatus = statusFilter.getValue();
                List<Auction> auctions = auctionApiService.getAll(currentPage, PAGE_SIZE, selectedStatus);

                cachedAuctions = auctions;
                cachedPage = currentPage;

                Platform.runLater(() -> {
                    statusFilter.setDisable(false);
                    renderAuctionUI(auctions);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    statusFilter.setDisable(false);
                    btnPrev.setDisable(currentPage == 0);
                    btnNext.setDisable(true);
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void renderAuctionUI(List<Auction> auctions) {
        auctionTilePane.getChildren().clear();

        List<Auction> filteredAuctions = new ArrayList<>(auctions);

        filteredAuctions.sort((a1, a2) -> {
            boolean isA1Active = a1.getStatus() == Auction.AuctionStatus.OPEN || a1.getStatus() == Auction.AuctionStatus.RUNNING;
            boolean isA2Active = a2.getStatus() == Auction.AuctionStatus.OPEN || a2.getStatus() == Auction.AuctionStatus.RUNNING;
            if (isA1Active && !isA2Active) return -1;
            if (!isA1Active && isA2Active) return 1;
            return 0;
        });

        try {
            for (Auction auction : filteredAuctions) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AuctionPageFXML/AuctionCard.fxml"));
                VBox card = loader.load(); // Chạy ở đây cực kỳ an toàn vì renderUI gọi từ Platform.runLater
                AuctionCardController cardController = loader.getController();
                cardController.setAuctionData(auction);
                auctionTilePane.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadingSpinner.setVisible(false);
        auctionTilePane.setVisible(true);
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(auctions.size() < PAGE_SIZE || filteredAuctions.isEmpty());
    }
}