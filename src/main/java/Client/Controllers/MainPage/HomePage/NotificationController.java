package Client.Controllers.MainPage.HomePage;

import Branch.AuctionManager;
import Branch.SessionManager;
import Branch.User;
import Client.Controllers.MainPage.ProfilePage.BaseController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;

import javafx.fxml.FXML;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javafx.scene.layout.VBox;

import javafx.stage.Popup;

public class NotificationController extends BaseController {

    private final User user = SessionManager.getCurrentUser();

    private final ObservableList<String> allNotifications =
            FXCollections.observableArrayList();

    private final Popup popup = new Popup();

    @FXML
    private Button notificationButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {

        loadNotifications();
    }

    @FXML
    private void showNotifications() {

        if (popup.isShowing()) {
            popup.hide();
            return;
        }

        ListView<String> popupList = new ListView<>();

        popupList.getItems().addAll(allNotifications);

        popupList.setPrefSize(340, 220);

        popupList.setStyle("""
            -fx-background-color: white;
            -fx-control-inner-background: white;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: #dbe3ff;
             """);

        Button clearButton = new Button("Clear All");

        clearButton.setStyle("""
            -fx-background-color: #2D3763;

            -fx-text-fill: white;

            -fx-background-radius: 8;

            -fx-cursor: hand;
        """);

        clearButton.setOnAction(event -> {

            allNotifications.clear();

            popupList.getItems().clear();

            updateNotificationCount();
        });

        VBox container = new VBox(12);

        container.getChildren().addAll(
                popupList,
                clearButton
        );

        container.setStyle("""
            -fx-background-color: white;
            -fx-padding: 15;
            -fx-background-radius: 14;
            -fx-border-radius: 14;

            -fx-border-color: #e2e8f0;

            -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.15),20,0,0,5);""");

        popup.getContent().clear();

        popup.getContent().add(container);

        Scene scene = notificationButton.getScene();

        popup.show(
                scene.getWindow(),
                notificationButton.localToScreen(0, 0).getX(),
                notificationButton.localToScreen(0, 0).getY() + 55
        );
    }

    private void loadNotifications() {

        if (user == null) return;

        Task<ObservableList<String>> task = new Task<>() {

            @Override
            protected ObservableList<String> call() {

                return FXCollections.observableArrayList(
                        user.getNotifications(
                                AuctionManager
                                        .getInstance()
                                        .getAllSessions()
                        )
                );
            }
        };

        task.setOnSucceeded(event -> {

            allNotifications.setAll(task.getValue());

            updateNotificationCount();
        });

        new Thread(task).start();
    }

    private void updateNotificationCount() {

        notificationButton.setText(
                "🔔 Notifications (" +
                        allNotifications.size() +
                        ")"
        );
    }

    @FXML
    private void hideNotifications() {

        popup.hide();
    }
}