package Client.Controllers.MainPage.ProfilePage;

import Branch.AuctionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class NotificationController extends BaseController {

    @FXML
    private ComboBox<String> filterBox;

    @FXML
    private ListView<String> notificationList;

    @FXML
    private Button clearBtn;

    private final ObservableList<String> allNotifications = FXCollections.observableArrayList();

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {

        setupFilter();

        notificationList.setItems(allNotifications);
    }

    @Override
    protected void initData() {

        loadNotifications();
    }

    private void setupFilter() {

        filterBox.getItems().addAll(
                "All",
                "Leading",
                "Outbid",
                "Won",
                "Lost"
        );

        filterBox.setValue("All");

        filterBox.setOnAction(event -> applyFilter());

        clearBtn.setOnAction(event -> clearAllNotifications());
    }

    private void loadNotifications() {

        if (user == null) return;

        loadingIndicator.setVisible(true);

        Task<ObservableList<String>> task = new Task<>() {

            @Override
            protected ObservableList<String> call() {

                return FXCollections.observableArrayList(
                        user.getNotifications(
                                AuctionManager.getInstance().getAllSessions()
                        )
                );
            }
        };

        task.setOnSucceeded(event -> {

            allNotifications.setAll(task.getValue());

            notificationList.setItems(allNotifications);

            loadingIndicator.setVisible(false);
        });

        task.setOnFailed(event -> {
            loadingIndicator.setVisible(false);
        });

        new Thread(task).start();
    }

    private void applyFilter() {

        String selected = filterBox.getValue();

        ObservableList<String> filtered =
                FXCollections.observableArrayList();

        for (String item : allNotifications) {

            switch (selected) {

                case "Leading" -> {
                    if (item.contains("LEADING")) {
                        filtered.add(item);
                    }
                }

                case "Outbid" -> {
                    if (item.contains("OUTBID")) {
                        filtered.add(item);
                    }
                }

                case "Won" -> {
                    if (item.contains("WON")) {
                        filtered.add(item);
                    }
                }

                case "Lost" -> {
                    if (item.contains("LOST")) {
                        filtered.add(item);
                    }
                }

                default -> filtered.add(item);
            }
        }

        notificationList.setItems(filtered);
    }

    private void clearAllNotifications() {

        allNotifications.clear();

        notificationList.getItems().clear();
    }
}