package Client.Controllers.MainPage.ProfilePage;

import Branch.AuctionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

public class NotificationController extends BaseController {

    @FXML
    private ComboBox<String> filterBox;

    @FXML
    private ListView<String> notificationList;

    @FXML
    private Button clearBtn;

    private final ObservableList<String> allNotifications = FXCollections.observableArrayList();

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
        System.out.println(user);

        allNotifications.clear();

        allNotifications.addAll(user.getNotifications(AuctionManager.getInstance().getAllSessions()));

        notificationList.setItems(allNotifications);

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