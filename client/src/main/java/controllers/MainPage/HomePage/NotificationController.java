package controllers.MainPage.HomePage;

import models.*;
import models.Common.AuctionAlert;
import controllers.MainPage.ProfilePage.BaseController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import java.net.URL;
import java.util.List;
import javafx.scene.control.ProgressIndicator;

public class NotificationController extends BaseController {
    private User getCurrentUser() {return SessionManager.getCurrentUser();}
    private final ObservableList<String> cachedNotifications = FXCollections.observableArrayList();
    private final Popup popup = new Popup();
    private boolean loaded = false;

    @FXML
    private Button notificationButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    private void setLoading(boolean loading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
        }

        notificationButton.setDisable(loading);
    }

    @FXML
    public void initialize() {
        popup.setAutoHide(true);

        updateNotificationCount();

        if (!loaded) {
            loadNotifications();
        }
    }
    @FXML
    private void showNotifications() {
        if (popup.isShowing()) {
            popup.hide();
            return;
        }

        Label titleLabel = new Label("Notifications");
        titleLabel.getStyleClass().add("notification-title");

        Button closeButton = new Button("✕");
        closeButton.getStyleClass().add("popup-close-button");
        closeButton.setOnAction(event -> popup.hide());

        HBox headerRow = new HBox();
        headerRow.getChildren().addAll(titleLabel, closeButton);
        headerRow.getStyleClass().add("notification-header-row");

        ListView<String> popupList = new ListView<>();
        popupList.setItems(cachedNotifications);
        popupList.getStyleClass().add("notification-list-view");

        Button clearButton = new Button("Clear All");
        clearButton.getStyleClass().add("button");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(event -> {
            cachedNotifications.clear();
            updateNotificationCount();
            popup.hide();
        });

        VBox container = new VBox();
        container.getChildren().addAll(headerRow, popupList, clearButton);
        container.getStyleClass().add("notification-popup-container");

        Scene scene = notificationButton.getScene();
        if (scene != null) {
            for (String stylesheetUrl : scene.getStylesheets()) {
                if (!container.getStylesheets().contains(stylesheetUrl)) {
                    container.getStylesheets().add(stylesheetUrl);
                }
            }

            URL cssResource = getClass().getResource("/MainFXML/HomePage/notification.css");
            if (cssResource != null) {
                String cssPath = cssResource.toExternalForm();
                if (!container.getStylesheets().contains(cssPath)) {
                    container.getStylesheets().add(cssPath);
                }
            }
        }

        popup.getContent().clear();
        popup.getContent().add(container);

        popup.show(
                scene.getWindow(),
                notificationButton.localToScreen(0, 0).getX() - 100,
                notificationButton.localToScreen(0, 0).getY() + notificationButton.getHeight() + 5
        );
    }

    private void loadNotifications() {
        User user = getCurrentUser();
        if (user == null || loaded) return;

        setLoading(true);

        Task<ObservableList<String>> task = new Task<>() {
            @Override
            protected ObservableList<String> call() {
                ObservableList<String> displayLines = FXCollections.observableArrayList();
                var globalAuctions = AuctionManager.getInstance().getAllSessions();

                List<AuctionAlert> rawAlerts = user.getNotifications(globalAuctions);

                for (AuctionAlert alert : rawAlerts) {
                    String line = switch (alert.type()) {
                        case LEADING             -> "🔥 LEADING | " + alert.itemName() + " | " + alert.currentPrice();
                        case OUTBID              -> "⚠️ OUTBID | " + alert.itemName() + " | " + alert.currentPrice();
                        case WON                 -> "🏆 WON | " + alert.itemName() + " | " + alert.currentPrice();
                        case LOST                -> "❌ LOST | " + alert.itemName() + " | " + alert.currentPrice();
                        case MY_AUCTION_RUNNING  -> "📢 MY AUCTION RUNNING | " + alert.itemName() + " | " + alert.currentPrice();
                        case MY_AUCTION_FINISHED -> "🏁 MY AUCTION FINISHED | " + alert.itemName() + " | Closed at: " + alert.currentPrice();
                    };
                    displayLines.add(line);
                }
                return displayLines;
            }
        };

        task.setOnSucceeded(event -> {
            cachedNotifications.setAll(task.getValue());
            loaded = true;
            updateNotificationCount();

            setLoading(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateNotificationCount() {
        notificationButton.setText("🔔 Notifications (" + cachedNotifications.size() + ")");
    }
}