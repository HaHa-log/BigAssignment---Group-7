package Client.Controllers.MainPage.ProfilePage;

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
        loadFakeHistory();

        historyList.setItems(allHistory);
    }

    private void setupFilter() {

        filterBox.getItems().addAll(
                "All",
                "Won",
                "Lost",
                "Owner"
        );

        filterBox.setValue("All");
    }
    //TEST DATA
    private void loadFakeHistory() {

        allHistory.addAll(

                "🏆 Won auction: iPhone 17 Pro Max | Final price: $1500",
                "❌ Lost auction: Gaming Laptop | Final price: $950",
                "📦 Your auction ended: Nike Air Jordan",
                "🏆 Won auction: Canon Camera | Final price: $500"
        );
    }

    private void applyFilter() {

        String selected = filterBox.getValue();
        ObservableList<String> filtered =
                FXCollections.observableArrayList();

        for (String item : allHistory) {

            switch (selected) {

                case "Won" -> {
                    if (item.contains("Won")) {
                        filtered.add(item);
                    }
                }

                case "Lost" -> {
                    if (item.contains("Lost")) {
                        filtered.add(item);
                    }
                }

                case "Owner" -> {
                    if (item.contains("Your auction")) {
                        filtered.add(item);
                    }
                }

                default -> filtered.add(item);
            }
        }

        historyList.setItems(filtered);
    }
}
