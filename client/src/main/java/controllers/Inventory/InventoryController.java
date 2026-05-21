package controllers.Inventory;

import models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class InventoryController {
    @FXML
    private VBox itemList;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ProgressIndicator loadingSpinner;

    /*
    private User user = SessionManager.getCurrentUser();
    // Fetch the current user's local inventory list
    private List<Item> items = ((Member) user).getInventory();

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
                javafx.application.Platform.runLater(() -> {
                    loadingSpinner.setVisible(false);
                    itemList.setVisible(true);
                });

                for (Item item : items) {
                    if (!selectedStatus.equals("ALL") && !item.getStatus().name().equals(selectedStatus)) {
                        continue;
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/InventoryFXML/ItemCard.fxml"));
                    HBox card = loader.load();
                    ItemCardController cardController = loader.getController();
                    cardController.setItemData(item);

                    javafx.application.Platform.runLater(() -> itemList.getChildren().add(card));
                }
            } catch (IOException e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> loadingSpinner.setVisible(false));
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

     */
}