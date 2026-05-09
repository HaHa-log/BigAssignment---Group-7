package Client.Controllers.MainPage.ProfilePage;

import Branch.AuctionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class HistoryController extends BaseController  {

    @FXML
    private ComboBox<String> filterBox;

    @FXML
    private ListView<String> historyList;

    private final ObservableList<String> allHistory =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        setupFilter();
        historyList.setItems(allHistory);
    }

    @Override
    protected void initData() {
        loadHistory();
    }

    private void setupFilter() {

        filterBox.getItems().addAll(
                "All",
                "Won",
                "Lost",
                "Owner"
        );

        filterBox.setValue("All");

        filterBox.setOnAction(event -> applyFilter());
    }
    private void loadHistory() {

        allHistory.clear();

        allHistory.addAll(

                user.getAuctionHistory(

                        AuctionManager
                                .getInstance()
                                .getAllSessions()
                )
        );
    }

    private void applyFilter() {

        String selected = filterBox.getValue();
        ObservableList<String> filtered =
                FXCollections.observableArrayList();

        for (String item : allHistory) {

            switch (selected) {

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

                case "Owner" -> {
                    if (item.contains("MY AUCTION")) {
                        filtered.add(item);
                    }
                }

                default -> filtered.add(item);
            }
        }

        historyList.setItems(filtered);
    }
}
