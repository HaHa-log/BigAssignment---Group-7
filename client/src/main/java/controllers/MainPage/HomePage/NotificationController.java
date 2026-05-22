package controllers.MainPage.HomePage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.dto.user.NotificationResponse;
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

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            protected ObservableList<String> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/users/"
                                + user.getId() + "/notifications"))
                        .GET().build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                List<NotificationResponse> alerts = mapper.readValue(
                        response.body(), new TypeReference<>() {}
                );

                ObservableList<String> lines = FXCollections.observableArrayList();
                for (NotificationResponse alert : alerts) {
                    String line = switch (alert.type()) {
                        case "LEADING"             -> "🔥 LEADING | " + alert.itemName() + " | " + alert.currentPrice();
                        case "OUTBID"              -> "⚠️ OUTBID | " + alert.itemName() + " | " + alert.currentPrice();
                        case "WON"                 -> "🏆 WON | " + alert.itemName() + " | " + alert.currentPrice();
                        case "LOST"                -> "❌ LOST | " + alert.itemName() + " | " + alert.currentPrice();
                        case "MY_AUCTION_RUNNING"  -> "📢 RUNNING | " + alert.itemName()+ " | " + alert.currentPrice();
                        case "MY_AUCTION_FINISHED" -> "🏁 FINISHED | " + alert.itemName() + " | Closed at: " + alert.currentPrice();
                        default -> alert.type() + " | " + alert.itemName();
                    };
                    lines.add(line);
                }
                return lines;
            }
        };

        task.setOnSucceeded(e -> {
            cachedNotifications.setAll(task.getValue());
            loaded = true;
            updateNotificationCount();
            setLoading(false);
        });
        task.setOnFailed(e -> {
            System.err.println("Failed to load notifications: " + task.getException().getMessage());
            setLoading(false);
        });

        new Thread(task).start();
    }

    private void updateNotificationCount() {
        notificationButton.setText("🔔 Notifications (" + cachedNotifications.size() + ")");
    }
}